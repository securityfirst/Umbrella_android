package org.secfirst.umbrella.whitelabel.data.disk

import com.fasterxml.jackson.annotation.JsonIgnore
import org.secfirst.umbrella.whitelabel.data.database.content.*
import org.secfirst.umbrella.whitelabel.data.database.form.Form


class Root(val elements: MutableList<Element> = arrayListOf(), val forms: MutableList<Form> = arrayListOf())

data class Element(
        var id: Long = 0,
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


val Element.convertToCategory: Category
    get() {
        val category = Category()
        category.checklist = this.checklist
        category.index = this.index
        category.description = this.description
        category.markdowns = this.markdowns
        category.path = this.path
        category.rootDir = this.rootDir
        category.title = this.title
        category.resourcePath = this.resourcePath
        return category
    }

val Element.convertToSubCategory: Subcategory
    get() {
        val subcategory = Subcategory()
        subcategory.checklist = this.checklist
        subcategory.index = this.index
        subcategory.description = this.description
        subcategory.markdowns = this.markdowns
        subcategory.path = this.path
        subcategory.rootDir = this.rootDir
        subcategory.title = this.title
        return subcategory
    }

val Element.convertToChild: Child
    get() {
        val child = Child()
        child.checklist = this.checklist
        child.index = this.index
        child.description = this.description
        child.markdowns = this.markdowns
        child.path = this.path
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
    val categories: MutableList<Category> = mutableListOf()
    var subCategories: MutableList<Subcategory> = mutableListOf()
    var children: MutableList<Child> = mutableListOf()

    this.elements.forEach { element ->
        val category = element.convertToCategory
        categories.add(category)
        element.children.forEach { subElement ->

            val subCategory = subElement.convertToSubCategory
            subCategories.add(subCategory)
            subElement.children.forEach { subElementChild ->

                val child = subElementChild.convertToChild
                children.add(child)
            }
            subCategory.children = children
            children = mutableListOf()
        }
        category.subcategories = subCategories
        subCategories = mutableListOf()
    }
    return ContentData(categories)
}

