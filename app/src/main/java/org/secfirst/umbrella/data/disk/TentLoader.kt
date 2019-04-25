package org.secfirst.umbrella.data.disk

import android.util.Log
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.content.ContentData
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.data.database.form.associateFormForeignKey
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.removeHead
import org.secfirst.umbrella.data.database.segment.replaceMarkdownImage
import org.secfirst.umbrella.feature.content.ContentService
import org.secfirst.umbrella.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.misc.parseYmlFile
import java.io.File
import javax.inject.Inject


class TentLoader @Inject constructor(private val tentRepo: TentRepo, contentService: ContentService? = null) {

    private val contentData = ContentData()
    private val elementMonitor: ElementSerializeMonitor? = contentService
    private var fileCount = 0
    private var listSize = 0

    suspend fun serializeContent(): ContentData {
        withContext(ioContext) {
            val predicateLanguage ="${getPathRepository()}${defaultTentLanguage()}"
            val files = tentRepo.loadElementsFile(predicateLanguage)
            val formFiles = tentRepo.loadFormFile(predicateLanguage)
            listSize = files.size + formFiles.size
            processFiles(files)
            loadForm(formFiles)
        }
        return contentData
    }

    private fun processFiles(files: List<File>) {
        files.forEach { file ->
            fileCount++
            val absolutePath = file.path.substringAfterLast(getPathRepository())
            val pwd = file.path.substringBeforeLast(file.name)
            when (getLevelOfPath(absolutePath)) {
                ELEMENT_LEVEL -> serializeElement(pwd, file)
                SUB_ELEMENT_LEVEL -> serializeSubElement(pwd, file)
                CHILD_LEVEL -> serializeChild(pwd, file)
            }
            calculatePercentage()
        }
    }

    private fun serializeElement(pwd: String, file: File) {
        val module = parseYmlFile(file, Module::class)
        updateCategories(module, file)
        filterSegmentFiles(pwd).forEach {
            val markdownText = it.readText().replaceMarkdownImage(pwd)
            val markdown = Markdown(it.path.substringAfterLast(getPathRepository()), markdownText).removeHead()
            markdown.module = module
            module.markdowns.add(markdown)
        }
        filterChecklistFiles(pwd).forEach {
            val newChecklist = parseYmlFile(it, Checklist::class)
            newChecklist.id = it.path.substringAfterLast(getPathRepository())
            newChecklist.module = module
            newChecklist.content.forEach { content ->
                content.checklist = newChecklist
            }
            module.checklist.add(newChecklist)
        }
    }

    private fun serializeSubElement(pwd: String, file: File) {
        val subject = parseYmlFile(file, Subject::class)
        updateCategories(subject, file)
        filterSegmentFiles(pwd).forEach {
            val markdownText = it.readText().replaceMarkdownImage(pwd)
            val markdown = Markdown(it.path.substringAfterLast(getPathRepository()), markdownText).removeHead()
            markdown.subject = subject
            subject.markdowns.add(markdown)
        }
        filterChecklistFiles(pwd).forEach {
            val newChecklist = parseYmlFile(it, Checklist::class)
            newChecklist.id = it.path.substringAfterLast(getPathRepository())
            newChecklist.subject = subject
            newChecklist.content.forEach { content ->
                content.checklist = newChecklist
            }
            subject.checklist.add(newChecklist)
        }
    }

    private fun serializeChild(pwd: String, file: File) {
        val difficulty = parseYmlFile(file, Difficulty::class)
        updateCategories(difficulty, file)
        filterSegmentFiles(pwd).forEach {
            val markdownText = it.readText().replaceMarkdownImage(pwd)
            val markdown = Markdown(it.path.substringAfterLast(getPathRepository()), markdownText).removeHead()
            markdown.difficulty = difficulty
            difficulty.markdowns.add(markdown)
        }
        filterChecklistFiles(pwd).forEach {
            val newChecklist = parseYmlFile(it, Checklist::class)
            newChecklist.id = it.path.substringAfterLast(getPathRepository())
            newChecklist.difficulty = difficulty
            newChecklist.content.forEach { content ->
                content.checklist = newChecklist
            }
            difficulty.checklist.add(newChecklist)
        }
    }

    private inline fun <reified T> updateCategories(obj: T, file: File) {
        val absolutePath = file.path.substringAfterLast(getPathRepository())
        val pwd = absolutePath.substringBeforeLast(file.name)
        Log.e("test", file.path)
        when (obj) {
            is Module -> {
                obj.id = pwd
                obj.resourcePath = obj.icon.filterImageCategoryFile()
                obj.rootDir = pwd.substringBeforeLast("/").substringAfterLast("/")
                contentData.modules.add(obj)
            }
            is Subject -> {
                val module = contentData.modules.last()
                obj.id = pwd
                obj.rootDir = pwd.substringBeforeLast("/").substringAfterLast("/")
                obj.module = module
                module.subjects.add(obj)
            }
            is Difficulty -> {
                val subject = contentData.modules.last().subjects.last()
                obj.id = pwd
                obj.rootDir = pwd.substringBeforeLast("/").substringAfterLast("/")
                obj.subject = subject
                subject.difficulties.add(obj)
            }
        }
    }

    private fun loadForm(formFiles: List<File>) {
        formFiles.forEach {
            val form = parseYmlFile(it, Form::class)
            form.path = it.path.substringAfterLast(getPathRepository())
            form.deeplinkTitle = form.title.toLowerCase()
            contentData.forms.add(form)
            fileCount++
            calculatePercentage()
        }
        contentData.forms.associateFormForeignKey()
    }

    private fun calculatePercentage() {
        val percentage = fileCount * 50 / listSize
        elementMonitor?.onSerializeProgress(percentage)
    }
}

interface ElementSerializeMonitor {
    fun onSerializeProgress(percentage: Int)
}