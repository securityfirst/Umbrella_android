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
        var template: String = "",
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
        val module = Module()
        module.id = this.pathId
        module.checklist = this.checklist
        module.index = this.index
        module.description = this.description
        module.markdowns = this.markdowns
        module.id = this.path
        module.rootDir = this.rootDir
        module.moduleTitle = this.title
        module.resourcePath = this.resourcePath
        module.template = this.template
        return module
    }

val Element.convertToSubject: Subject
    get() {
        val subject = Subject()
        subject.id = pathId
        subject.checklist = checklist
        subject.index = index
        subject.description = description
        subject.markdowns = markdowns
        subject.id = path
        subject.rootDir = rootDir
        subject.title = title
        return subject
    }

val Element.convertToDifficulty: Difficulty
    get() {
        val difficulty = Difficulty()
        difficulty.id = this.pathId
        difficulty.checklist = this.checklist
        difficulty.index = this.index
        difficulty.description = this.description
        difficulty.markdowns = this.markdowns
        difficulty.id = this.path
        difficulty.rootDir = this.rootDir
        difficulty.title = this.title
        return difficulty
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
        val module = element.convertToModule
        modules.add(module)
        element.children.forEach { subElement ->

            val subject = subElement.convertToSubject
            subCategories.add(subject)
            subElement.children.forEach { subElementChild ->

                val difficulty = subElementChild.convertToDifficulty
                difficulties.add(difficulty)
            }
            subject.difficulties = difficulties
            difficulties = mutableListOf()
        }
        module.subjects = subCategories
        subCategories = mutableListOf()
    }
    return ContentData(modules)
}

