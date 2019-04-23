package org.secfirst.umbrella.data.disk

import com.fasterxml.jackson.annotation.JsonIgnore
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.data.database.segment.Markdown


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
        module.title = this.title
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