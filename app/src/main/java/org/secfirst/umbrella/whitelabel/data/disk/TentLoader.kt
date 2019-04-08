package org.secfirst.umbrella.whitelabel.data.disk

import android.util.Log
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.content.ContentData
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.form.associateFormForeignKey
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.removeHead
import org.secfirst.umbrella.whitelabel.data.database.segment.replaceMarkdownImage
import org.secfirst.umbrella.whitelabel.feature.content.ContentService
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.deviceLanguage
import org.secfirst.umbrella.whitelabel.misc.parseYmlFile
import java.io.File
import javax.inject.Inject


class TentLoader @Inject constructor(private val tentRepo: TentRepo, contentService: ContentService? = null) {

    private val contentData = ContentData()
    private val elementMonitor: ElementSerializeMonitor? = contentService
    private var fileCount = 0
    private var listSize = 0

    suspend fun serializeContent(): ContentData {
        withContext(ioContext) {
            val files = tentRepo.loadElementsFile()
            val formFiles = tentRepo.loadFormFile()
            listSize = files.size + formFiles.size
            processFiles(files)
            loadForm(formFiles)
        }
        return contentData
    }

    private fun processFiles(files: List<File>) {
        files.forEach { file ->
            fileCount++
            val absolutePath = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")
            val pwd = file.path.substringBeforeLast("/${file.name}")
            when (getLevelOfPath(absolutePath)) {
                ELEMENT_LEVEL -> serializeElement(pwd, absolutePath, file)
                SUB_ELEMENT_LEVEL -> serializeSubElement(pwd, absolutePath, file)
                CHILD_LEVEL -> serializeChild(pwd, absolutePath, file)
            }
            calculatePercentage()
        }
    }

    private fun serializeElement(pwd: String, absolutePath: String, file: File) {
        val module = parseYmlFile(file, Module::class)
        updateCategories(module, file)
        filterSegmentFiles(pwd).forEach {
            val markdownText = it.readText().replaceMarkdownImage(absolutePath)
            val markdown = Markdown(it.path, markdownText).removeHead()
            markdown.module = module
            module.markdowns.add(markdown)
        }
        filterChecklistFiles(pwd).forEach {
            val newChecklist = parseYmlFile(it, Checklist::class)
            newChecklist.id = it.path
            newChecklist.module = module
            module.checklist.add(newChecklist)
        }
    }

    private fun serializeSubElement(pwd: String, absolutePath: String, file: File) {
        val subject = parseYmlFile(file, Subject::class)
        updateCategories(subject, file)
        filterSegmentFiles(pwd).forEach {
            val markdownText = it.readText().replaceMarkdownImage(absolutePath)
            val markdown = Markdown(it.path, markdownText).removeHead()
            markdown.subject = subject
            subject.markdowns.add(markdown)
        }
        filterChecklistFiles(pwd).forEach {
            val newChecklist = parseYmlFile(it, Checklist::class)
            newChecklist.id = it.path
            newChecklist.subject = subject
            subject.checklist.add(newChecklist)
        }
    }

    private fun serializeChild(pwd: String, absolutePath: String, file: File) {
        val difficulty = parseYmlFile(file, Difficulty::class)
        updateCategories(difficulty, file)
        filterSegmentFiles(pwd).forEach {
            val markdownText = it.readText().replaceMarkdownImage(absolutePath)
            val markdown = Markdown(it.path, markdownText).removeHead()
            markdown.difficulty = difficulty
            difficulty.markdowns.add(markdown)
        }
        filterChecklistFiles(pwd).forEach {
            val newChecklist = parseYmlFile(it, Checklist::class)
            newChecklist.id = it.path
            newChecklist.difficulty = difficulty
            difficulty.checklist.add(newChecklist)
        }
    }

    private inline fun <reified T> updateCategories(obj: T, file: File) {
        val absolutePath = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")
        val pwd = absolutePath.substringBeforeLast("/${file.name}")
        when (obj) {
            is Module -> {
                obj.id = file.path
                obj.resourcePath = obj.icon.filterImageCategoryFile()
                obj.rootDir = pwd
                contentData.modules.add(obj)
            }
            is Subject -> {
                val module = contentData.modules.last()
                obj.id = file.path
                obj.rootDir = pwd
                obj.module = module
                module.subjects.add(obj)
                Log.d("test", "id - ${obj.id}")
            }
            is Difficulty -> {
                val subject = contentData.modules.last().subjects.last()
                obj.id = file.path
                obj.rootDir = pwd
                obj.subject = subject
                subject.difficulties.add(obj)
            }
        }
    }

    private fun loadForm(formFiles: List<File>) {
        formFiles.forEach {
            val form = parseYmlFile(it, Form::class)
            form.path = it.path
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