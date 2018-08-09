package org.secfirst.umbrella.whitelabel.data.database.content

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface ContentDao {

    suspend fun insertAllLessons(root: Root) {
        withContext(AppExecutors.ioContext) {
            val dataLesson = root.convertRootToLesson()

            dataLesson.categories.forEach { category ->
                category.associateForeignKey(category)
                modelAdapter<Category>().save(category)
            }

            dataLesson.categories.walkChild { child ->
                modelAdapter<Child>().save(child)
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
}