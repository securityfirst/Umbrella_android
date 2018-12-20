package org.secfirst.umbrella.whitelabel.data.database.content

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.BaseDao
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.walkThroughDifficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.form.Item
import org.secfirst.umbrella.whitelabel.data.database.form.associateFormForeignKey
import org.secfirst.umbrella.whitelabel.data.database.lesson.*
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.data.disk.convertTo
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface ContentDao : BaseDao {

    suspend fun insertAllLessons(root: Root) {
        withContext(ioContext) {
            val dataLesson = root.convertTo()

            modelAdapter<Module>().save(createDefaultFavoriteModule())

            dataLesson.modules.forEach { module ->
                module.associateForeignKey()
                modelAdapter<Module>().save(module)
            }

            dataLesson.modules.walkThroughSubject { subject ->
                modelAdapter<Subject>().save(subject)
                modelAdapter<Markdown>().saveAll(subject.markdowns)
                modelAdapter<Checklist>().saveAll(subject.checklist)
            }

            dataLesson.modules.walkThroughDifficulty { child ->
                modelAdapter<Difficulty>().save(child)
                insertChecklistContent(child.checklist)
            }
            insertFormsContent(root.forms)
        }


    }

    private fun insertChecklistContent(checklist: MutableList<Checklist>) {
        checklist.forEach { it ->
            it.content.forEach { content ->
                modelAdapter<Content>().save(content)
            }
        }
    }

    private fun insertFormsContent(forms: MutableList<Form>) {
        forms.associateFormForeignKey()
        forms.forEach { form ->
            modelAdapter<Form>().save(form)
        }
        forms.forEach { form ->
            form.screens.forEach { screen ->
                screen.items.forEach { item ->
                    modelAdapter<Item>().save(item)
                }
            }
        }
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

    suspend fun resetContent() {
        withContext(ioContext) {
            val cacheDir = FlowManager.getContext().cacheDir
            FileUtils.deleteQuietly(cacheDir)
            FlowManager.getDatabase(AppDatabase.NAME).reset()
        }
    }
}
