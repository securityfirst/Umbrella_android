package org.secfirst.umbrella.whitelabel.feature.content.presenter

import com.raizlabs.android.dbflow.config.FlowManager
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.removeHead
import org.secfirst.umbrella.whitelabel.data.database.segment.replaceMarkdownImage
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.CHILD_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.SUB_ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.getDelimiter
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.whitelabel.misc.AppExecutors
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import org.secfirst.umbrella.whitelabel.serialize.PathUtils
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getLevelOfPath
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import org.secfirst.umbrella.whitelabel.serialize.parseYmlFile
import java.io.File
import javax.inject.Inject


class ContentPresenterImp<V : ContentView, I : ContentBaseInteractor>
@Inject internal constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), ContentBasePresenter<V, I> {


    override fun updateContent(pairFiles: List<Pair<String, File>>) {
        launchSilent(uiContext) {
            val checklists = mutableListOf<Checklist>()
            val markdowns = mutableListOf<Markdown>()
            val forms = mutableListOf<Form>()
            val modules = mutableListOf<Module>()
            val subjects = mutableListOf<Subject>()
            val difficulties = mutableListOf<Difficulty>()

            interactor?.let {
                pairFiles.forEach { pair ->
                    val file = pair.second
                    val sha1ID = pair.first
                    val absoluteFilePath = file.path.substringAfterLast(PathUtils.basePath(), "")
                    val pwd = getWorkDirectory(absoluteFilePath)

                    when (getDelimiter(file.nameWithoutExtension)) {
                        TypeFile.SEGMENT.value -> {
                            val markdownFormatted = file.readText().replaceMarkdownImage(pwd)
                            val newMarkdown = Markdown(sha1ID, markdownFormatted).removeHead()
                            val oldMarkdown = it.getMarkdown(sha1ID)
                            oldMarkdown?.let { oldMarkdownSafe ->
                                markdowns.add(updateMarkdownForeignKey(newMarkdown, oldMarkdownSafe))
                            }
                        }
                        TypeFile.CHECKLIST.value -> {
                            val newChecklist = parseYmlFile(file, Checklist::class)
                            newChecklist.sha1ID = sha1ID
                            val oldChecklist = it.getChecklist(sha1ID)
                            oldChecklist?.let { oldChecklistSafe ->
                                checklists.add(updateChecklistForeignKey(newChecklist, oldChecklistSafe))
                            }

                        }
                        TypeFile.FORM.value -> {
                            val newForm = parseYmlFile(file, Form::class)
                            newForm.sh1ID = sha1ID
                            val oldForm = it.getForm(sha1ID)
                            oldForm?.let { oldFormSafe -> updateFormForeignKey(newForm, oldFormSafe) }
                            forms.add(newForm)
                        }
                        else -> {
                            when (getLevelOfPath(pwd)) {
                                ELEMENT_LEVEL -> {
                                    val newElement = parseYmlFile(file, Element::class)
                                    newElement.sh1ID = sha1ID
                                    val module = newElement.convertToModule
                                    modules.add(module)
                                }
                                SUB_ELEMENT_LEVEL -> {
                                    val newElement = parseYmlFile(file, Element::class)
                                    newElement.sh1ID = sha1ID
                                    val subject = newElement.convertToSubCategory
                                    subjects.add(subject)
                                }
                                CHILD_LEVEL -> {
                                    val newElement = parseYmlFile(file, Element::class)
                                    newElement.sh1ID = sha1ID
                                    val difficulty = newElement.convertToDifficulty
                                    difficulties.add(difficulty)
                                }
                            }
                        }
                    }
                }
                it.saveAllMarkdowns(markdowns)
                it.saveAllChecklists(checklists)
                it.saveAllForms(forms)
                it.saveAllModule(modules)
                it.saveAllDifficulties(difficulties)
                it.saveAllSubjects(subjects)
                getView()?.downloadContentCompleted(true)
            }
        }
    }

    override fun manageContent() {
        var isFetchData: Boolean
        launchSilent(AppExecutors.uiContext) {
            interactor?.let {
                getView()?.downloadContentInProgress()
                isFetchData = it.fetchData()

                if (isFetchData) {
                    val root = it.initParser()
                    it.persist(root)
                }
                it.persistFeedSource(createFeedSources())
                getView()?.downloadContentCompleted(isFetchData)
            }
        }
    }

    private fun updateChecklistForeignKey(newChecklist: Checklist, oldChecklist: Checklist): Checklist {
        newChecklist.module = oldChecklist.module
        newChecklist.subject = oldChecklist.subject
        newChecklist.difficulty = oldChecklist.difficulty
        return newChecklist
    }

    private fun updateMarkdownForeignKey(newMarkdown: Markdown, oldMarkdown: Markdown): Markdown {
        newMarkdown.module = oldMarkdown.module
        newMarkdown.subject = oldMarkdown.subject
        newMarkdown.difficulty = oldMarkdown.difficulty
        return newMarkdown
    }

    private fun updateFormForeignKey(newForm: Form, oldForm: Form): Form {
        for (i in newForm.screens.indices) {
            newForm.screens[i].form = oldForm.screens[i].form
            newForm.screens[i].id = oldForm.screens[i].id
            for (j in newForm.screens[i].items.indices) {
                val newItem = newForm.screens[i].items[j]
                val oldItem = oldForm.screens[i].items[j]
                newItem.id = oldItem.id
                newItem.screen = oldItem.screen
                for (y in newForm.screens[i].items[j].options.indices) {
                    val newOption = newForm.screens[i].items[j].options[y]
                    val oldOption = oldForm.screens[i].items[j].options[y]
                    newOption.id = oldOption.id
                    newOption.item = oldOption.item
                }
            }
        }
        return newForm
    }

    private fun createFeedSources(): List<FeedSource> {
        val feedSources = mutableListOf<FeedSource>()
        val feedSource1 = FeedSource("ReliefWeb", false, 0)
        val feedSource2 = FeedSource("UN", false, 1)
        val feedSource3 = FeedSource("FCO", false, 2)
        val feedSource4 = FeedSource("CDC", false, 3)
        val feedSource5 = FeedSource("Global Disaster Alert\nCoordination System", false, 4)
        val feedSource6 = FeedSource("US State Department Country\nWarnings", false, 5)
        feedSources.add(feedSource1)
        feedSources.add(feedSource2)
        feedSources.add(feedSource3)
        feedSources.add(feedSource4)
        feedSources.add(feedSource5)
        feedSources.add(feedSource6)
        return feedSources
    }

    override fun cleanContent() {
        val cacheDir = UmbrellaApplication.instance.cacheDir
        FileUtils.deleteQuietly(cacheDir)
        FlowManager.getDatabase(AppDatabase.NAME).reopen()
        getView()?.onCleanDatabaseSuccess()
    }


}