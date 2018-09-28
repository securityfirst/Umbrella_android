package org.secfirst.umbrella.whitelabel.data.database

import com.raizlabs.android.dbflow.rx2.structure.BaseRXModel
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.content.Checklist
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown

open class BaseModel : BaseRXModel() {
    fun associateForeignKey(module: Module) {

        associateMarkdown(module.markdowns, module)
        associateChecklist(module.checklist, module)

        module.subjects.forEach { subcategory ->
            subcategory.module = module
            associateMarkdown(subcategory.markdowns, subcategory)
            associateChecklist(subcategory.checklist, subcategory)
            subcategory.difficulties.forEach { child ->
                child.subject = subcategory
                associateChecklist(child.checklist, child)
                associateMarkdown(child.markdowns, child)
            }
        }
    }



    private inline fun <reified T> associateChecklist(checklists: MutableList<Checklist>, foreignKey: T) {
        checklists.forEach { checklist ->
            when (foreignKey) {
                is Module -> checklist.module = foreignKey
                is Subject -> checklist.subject = foreignKey
                is Difficulty -> checklist.difficulty = foreignKey
            }
            checklist.content.forEach { content ->
                content.checklist = checklist
            }
        }
    }

    private inline fun <reified T> associateMarkdown(markdowns: MutableList<Markdown>, foreignKey: T) {
        markdowns.forEach { mark ->
            when (foreignKey) {
                is Module -> mark.module = foreignKey
                is Subject -> mark.subject = foreignKey
                is Difficulty -> mark.difficulty = foreignKey
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