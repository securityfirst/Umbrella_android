package org.secfirst.umbrella.data.database.content

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.BaseDao
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Content
import org.secfirst.umbrella.data.database.checklist.ContentMonitor
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.data.database.form.Item
import org.secfirst.umbrella.data.database.form.Option
import org.secfirst.umbrella.data.database.form.Screen
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.data.database.lesson.createDefaultFavoriteModule
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.database.reader.RSS
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.sortByIndex
import org.secfirst.umbrella.misc.AppExecutors.Companion.ioContext

interface ContentDao : BaseDao, ContentMonitor {
    suspend fun insertAllContent(contentData: ContentData) {
        withContext(ioContext) {
            val modules = contentData.modules
            val totalFiles = contentData.modules.size + contentData.forms.size
            var lessonCount = 0
            modelAdapter<Module>().save(createDefaultFavoriteModule())
            modelAdapter<Module>().saveAll(modules)
            modules.forEach { module ->
                module.markdowns = module.markdowns.sortByIndex()
                modelAdapter<Markdown>().saveAll(module.markdowns)
                modelAdapter<Checklist>().saveAll(module.checklist)
                insertChecklistContent(module.checklist)
                module.subjects.forEach { subject ->
                    subject.markdowns = subject.markdowns.sortByIndex()
                    modelAdapter<Subject>().save(subject)
                    modelAdapter<Markdown>().saveAll(subject.markdowns)
                    modelAdapter<Checklist>().saveAll(subject.checklist)
                    subject.difficulties.forEach { difficulty ->
                        difficulty.markdowns = difficulty.markdowns.sortByIndex()
                        modelAdapter<Difficulty>().save(difficulty)
                        modelAdapter<Markdown>().saveAll(difficulty.markdowns)
                        insertChecklistContent(difficulty.checklist)
                    }
                }
                calculatePercentage(++lessonCount, totalFiles)
            }
            insertAllForms(contentData.forms, lessonCount, totalFiles)
        }
    }

    private fun insertChecklistContent(checklist: MutableList<Checklist>) {
        checklist.forEach {
            it.content.forEach { content ->
                modelAdapter<Content>().save(content)
            }
        }
    }

    private fun insertAllForms(forms: MutableList<Form>, fileCount: Int, fileSize: Int) {
        var formCount = fileCount
        modelAdapter<Form>().saveAll(forms)
        forms.forEach { form ->
            ++formCount
            modelAdapter<Screen>().saveAll(form.screens)
            form.screens.forEach { screen ->
                modelAdapter<Item>().saveAll(screen.items)
                screen.items.forEach { item ->
                    modelAdapter<Option>().saveAll(item.options)
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
        val percentage = if(listSize != 0) fileCount * 100 / listSize else 0
        onContentProgress(percentage)
    }
}
