package org.secfirst.umbrella.whitelabel.data.database.content

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseDao
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.checklist.ContentMonitor
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.form.Item
import org.secfirst.umbrella.whitelabel.data.database.form.associateFormForeignKey
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.createDefaultFavoriteModule
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.sortByIndex
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface ContentDao : BaseDao, ContentMonitor {
    suspend fun insertAllLessons(root: Root) {
        withContext(ioContext) {
            val lessonCount = insertLessons(root.modules)
            insertFormsContent(root.forms, lessonCount.first, lessonCount.second)
        }
    }

    private suspend fun insertLessons(modules: List<Module>): Pair<Int, Int> {
        var lessonCount = 0
        val lessonSize = modules.size
        withContext(ioContext) {
            modelAdapter<Module>().save(createDefaultFavoriteModule())
            modelAdapter<Module>().saveAll(modules)
            modules.forEach { module ->
                modelAdapter<Markdown>().saveAll(module.markdowns)
                module.subjects.forEach { subject ->
                    modelAdapter<Subject>().save(subject)
                    modelAdapter<Markdown>().saveAll(subject.markdowns)
                    modelAdapter<Checklist>().saveAll(subject.checklist)
                    subject.difficulties.forEach { difficulty ->
                        difficulty.markdowns = difficulty.markdowns.sortByIndex().toMutableList()
                        modelAdapter<Difficulty>().save(difficulty)
                        insertChecklistContent(difficulty.checklist)
                    }
                }
                calculatePercentage(++lessonCount, lessonSize)
            }
        }
        return Pair(lessonCount, lessonSize)
    }

    private fun insertChecklistContent(checklist: MutableList<Checklist>) {
        checklist.forEach {
            it.content.forEach { content ->
                modelAdapter<Content>().save(content)
            }
        }
    }

    private fun insertFormsContent(forms: MutableList<Form>, fileCount: Int, fileSize: Int) {
        var formCount = fileCount
        forms.associateFormForeignKey()
        forms.forEach { form ->
            modelAdapter<Form>().save(form)
            ++formCount
        }
        forms.forEach { form ->
            form.screens.forEach { screen ->
                screen.items.forEach { item ->
                    modelAdapter<Item>().save(item)
                }
            }
        }
        calculatePercentage(formCount, fileSize)
    }

    suspend fun insertFeedSource(feedSources: List<FeedSource>) {
        withContext(ioContext) {
            modelAdapter<FeedSource>().saveAll(feedSources)
        }
    }

    suspend fun insertDefaultRSS(rssList: List<RSS>) {
        withContext(ioContext) {
            modelAdapter<RSS>().saveAll(rssList)
        }
    }

    suspend fun resetContent(): Boolean {
        var res = false
        withContext(ioContext) {
            res = try {
                FileUtils.deleteDirectory(FlowManager.getContext().cacheDir)
                FlowManager.getDatabase(AppDatabase.NAME).close()
                FlowManager.getDatabase(AppDatabase.NAME).destroy()
                true
            } catch (e: Exception) {
                false
            }
        }
        return res
    }

    private fun calculatePercentage(fileCount: Int, listSize: Int) {
        val percentage = fileCount * 100 / listSize
        onContentProgress(percentage)
    }
}
