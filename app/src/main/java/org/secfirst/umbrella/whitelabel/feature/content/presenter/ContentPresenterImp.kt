package org.secfirst.umbrella.whitelabel.feature.content.presenter

import com.raizlabs.android.dbflow.config.FlowManager
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.CHILD_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.SUB_ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.getPathRepository
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.whitelabel.misc.AppExecutors
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
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
            interactor?.let {
                pairFiles.forEach { pair ->

                    val file = pair.second
                    val sha1ID = pair.first
                    val absoluteFilePath = getPathRepository() + file.absolutePath
                    val pwd = getWorkDirectory(absoluteFilePath)

                    when (absoluteFilePath) {
                        TypeFile.SEGMENT.value -> {
                            val newSegment = parseYmlFile(file, Markdown::class)
                            newSegment.sha1ID = sha1ID
                        }
                        TypeFile.CHECKLIST.value -> {
                            val newChecklist = parseYmlFile(file, Checklist::class)
                            newChecklist.sha1ID = sha1ID
                        }
                        TypeFile.FORM.value -> {
                            val newForm = parseYmlFile(file, Form::class)
                            newForm.sh1ID = sha1ID
                        }
                    }

                    when (getLevelOfPath(pwd)) {
                        ELEMENT_LEVEL -> {
                            val newElement = parseYmlFile(file, Element::class)
                            newElement.sh1ID = sha1ID
                            newElement.convertToModule
                        }
                        SUB_ELEMENT_LEVEL -> {
                            val newElement = parseYmlFile(file, Element::class)
                            newElement.sh1ID = sha1ID
                            newElement.convertToSubCategory
                        }
                        CHILD_LEVEL -> {
                            val newElement = parseYmlFile(file, Element::class)
                            newElement.sh1ID = sha1ID
                            newElement.convertToDifficulty
                        }
                    }
                }
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

    override fun cleanContent() {
        val cacheDir = UmbrellaApplication.instance.cacheDir
        FileUtils.deleteQuietly(cacheDir)
        FlowManager.getDatabase(AppDatabase.NAME).reopen()
        getView()?.onCleanDatabaseSuccess()
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
}