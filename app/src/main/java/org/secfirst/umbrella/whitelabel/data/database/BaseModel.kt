package org.secfirst.umbrella.whitelabel.data.database

import com.raizlabs.android.dbflow.rx2.structure.BaseRXModel
import org.secfirst.umbrella.whitelabel.data.*
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

open class BaseModel : BaseRXModel() {
    fun associateForeignKey(category: Category) {

        associateMarkdown(category.markdowns, category)
        associateChecklist(category.checklist, category)

        category.subcategories.forEach { subcategory ->
            subcategory.category = category
            associateMarkdown(subcategory.markdowns, subcategory)
            associateChecklist(subcategory.checklist, subcategory)
            subcategory.children.forEach { child ->
                child.subcategory = subcategory
                associateChecklist(child.checklist, child)
                associateMarkdown(child.markdowns, child)
            }
        }
    }

    private inline fun <reified T> associateChecklist(checklists: MutableList<Checklist>, foreignKey: T) {
        checklists.forEach { checklist ->
            when (foreignKey) {
                is Category -> checklist.category = foreignKey
                is Subcategory -> checklist.subcategory = foreignKey
                is Child -> checklist.child = foreignKey
            }
            checklist.content.forEach { content ->
                content.checklist = checklist
            }
        }
    }

    private inline fun <reified T> associateMarkdown(markdowns: MutableList<Markdown>, foreignKey: T) {
        markdowns.forEach { mark ->
            when (foreignKey) {
                is Category -> mark.category = foreignKey
                is Subcategory -> mark.subcategory = foreignKey
                is Child -> mark.child = foreignKey
            }
        }

    }

    fun associateFormForeignKey(forms: MutableList<Form>) {
        forms.forEach { form ->
            form.screens.forEach { screen ->
                screen.form = form
                screen.items.forEach { item ->
                    item.screen = screen
                    item.options.forEach { option -> option.item = item }
                }
            }
        }
    }
}