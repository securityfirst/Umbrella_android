package org.secfirst.umbrella.whitelabel.data.disk

import com.fasterxml.jackson.annotation.JsonIgnore
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.content.ContentData
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown


class Root(val elements: MutableList<Element> = arrayListOf(), val forms: MutableList<Form> = arrayListOf())

data class Element(
        var pathId: String = "",
        var index: Int = 0,
        var title: String = "",
        var description: String = "",
        var markdowns: MutableList<Markdown> = arrayListOf(),
        var children: MutableList<Element> = arrayListOf(),
        var checklist: MutableList<Checklist> = arrayListOf(),
        var rootDir: String = "",
        var icon: String = "",
        var path: String = "",
        @JsonIgnore
        var resourcePath: String = "")


val Element.convertToModule: Module
    get() {
        val category = Module()
        category.id = this.pathId
        category.checklist = this.checklist
        category.index = this.index
        category.description = this.description
        category.markdowns = this.markdowns
        category.id = this.path
        category.rootDir = this.rootDir
        category.moduleTitle = this.title
        category.resourcePath = this.resourcePath
        return category
    }

val Element.convertToSubCategory: Subject
    get() {
        val subcategory = Subject()
        subcategory.id = this.pathId
        subcategory.checklist = this.checklist
        subcategory.index = this.index
        subcategory.description = this.description
        subcategory.markdowns = this.markdowns
        subcategory.id = this.path
        subcategory.rootDir = this.rootDir
        subcategory.title = this.title
        return subcategory
    }

val Element.convertToDifficulty: Difficulty
    get() {
        val child = Difficulty()
        child.id = this.pathId
        child.checklist = this.checklist
        child.index = this.index
        child.description = this.description
        child.markdowns = this.markdowns
        child.id = this.path
        child.rootDir = this.rootDir
        child.title = this.title
        return child
    }

inline fun MutableList<Element>.walkSubElement(action: (Element) -> Unit) {
    this.forEach { element ->
        element.children.forEach(action)
    }
}

inline fun MutableList<Element>.walkChild(action: (Element) -> Unit) {
    this.forEach { element ->
        element.children.forEach { subElement ->
            subElement.children.forEach(action)
        }
    }
}

fun Root.convertTo(): ContentData {
    val modules: MutableList<Module> = mutableListOf()
    var subCategories: MutableList<Subject> = mutableListOf()
    var difficulties: MutableList<Difficulty> = mutableListOf()

    this.elements.forEach { element ->
        val category = element.convertToModule
        modules.add(category)
        element.children.forEach { subElement ->

            val subCategory = subElement.convertToSubCategory
            subCategories.add(subCategory)
            subElement.children.forEach { subElementChild ->

                val child = subElementChild.convertToDifficulty
                difficulties.add(child)
            }
            subCategory.difficulties = difficulties
            difficulties = mutableListOf()
        }
        category.subjects = subCategories
        subCategories = mutableListOf()
    }
    return ContentData(modules)
}

