package org.secfirst.umbrella.whitelabel.data.database.content

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.walkThroughDifficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.form.Item
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.walkThroughSubject
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.data.disk.convertTo
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface ContentDao {

    suspend fun insertAllLessons(root: Root) {
        withContext(ioContext) {
            val dataLesson = root.convertTo()

            dataLesson.modules.forEach { module ->
                module.associateForeignKey(module)
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
        forms.forEach { form ->
            form.associateFormForeignKey(forms)
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
}