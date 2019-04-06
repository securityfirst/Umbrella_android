package org.secfirst.umbrella.whitelabel.data.disk

import android.util.Log
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
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

    private val root = Root()
    private val elementMonitor: ElementSerializeMonitor? = contentService
    private var fileCount = 0
    private var listSize = 0

    suspend fun process(): Root {
        withContext(ioContext) {
            val categoriesFiles = tentRepo.loadElementsFile()
            listSize = categoriesFiles.size
            categoriesFiles.forEach {
                fileCount++
                calculatePercentage()
                serializeFiles(it)
            }
        }
        return root
    }

    private fun serializeFiles(file: File) {
        val absolutePath = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")
        val pwd = file.path.substringBeforeLast("/${file.name}")
        when (getLevelOfPath(absolutePath)) {
            ELEMENT_LEVEL -> {
                val module = parseYmlFile(file, Module::class)
                updateCategories(module, file)
                filterSegmentAndChecklistFiles(pwd).forEach {
                    when (it.nameWithoutExtension.substringBeforeLast("_")) {
                        TypeFile.SEGMENT.value -> {
                            val markdownText = it.readText().replaceMarkdownImage(absolutePath)
                            val markdown = Markdown(it.path, markdownText).removeHead()
                            markdown.module = module
                            module.markdowns.add(markdown)
                        }
                        TypeFile.CHECKLIST.value -> {
                            val newChecklist = parseYmlFile(it, Checklist::class)
                            newChecklist.id = it.path
                            newChecklist.module = module
                            module.checklist.add(newChecklist)
                        }
                    }
                }
            }
            SUB_ELEMENT_LEVEL -> {
                val subject = parseYmlFile(file, Subject::class)
                updateCategories(subject, file)
                filterSegmentAndChecklistFiles(pwd).forEach {
                    when (it.nameWithoutExtension.substringBeforeLast("_")) {
                        TypeFile.SEGMENT.value -> {
                            val markdownText = it.readText().replaceMarkdownImage(absolutePath)
                            val markdown = Markdown(it.path, markdownText).removeHead()
                            markdown.subject = subject
                            subject.markdowns.add(markdown)
                        }
                        TypeFile.CHECKLIST.value -> {
                            val newChecklist = parseYmlFile(it, Checklist::class)
                            newChecklist.id = it.path
                            newChecklist.subject = subject
                            subject.checklist.add(newChecklist)
                        }
                    }
                }
            }
            CHILD_LEVEL -> {
                val difficulty = parseYmlFile(file, Difficulty::class)
                updateCategories(difficulty, file)
                filterSegmentAndChecklistFiles(pwd).forEach {
                    when (it.nameWithoutExtension.substringBeforeLast("_")) {
                        TypeFile.SEGMENT.value -> {
                            val markdownText = it.readText().replaceMarkdownImage(absolutePath)
                            val markdown = Markdown(it.path, markdownText).removeHead()
                            markdown.difficulty = difficulty
                            difficulty.markdowns.add(markdown)
                        }
                        TypeFile.CHECKLIST.value -> {
                            val newChecklist = parseYmlFile(it, Checklist::class)
                            newChecklist.id = it.path
                            newChecklist.difficulty = difficulty
                            difficulty.checklist.add(newChecklist)
                        }
                    }
                }
            }
        }
    }

    private fun test() {

    }

    private inline fun <reified T> updateCategories(obj: T, file: File) {
        val absolutePath = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")
        val pwd = absolutePath.substringBeforeLast("/${file.name}")
        when (obj) {
            is Module -> {
                obj.id = file.path
                obj.resourcePath = obj.icon.filterImageCategoryFile()
                obj.rootDir = pwd
                root.modules.add(obj)
            }
            is Subject -> {
                val module = root.modules.last()
                obj.id = file.path
                obj.rootDir = pwd
                obj.module = module
                module.subjects.add(obj)
                Log.d("test", "id - ${obj.id}")
            }
            is Difficulty -> {
                val subject = root.modules.last().subjects.last()
                obj.id = file.path
                obj.rootDir = pwd
                obj.subject = subject
                subject.difficulties.add(obj)
            }
        }
    }

    private fun calculatePercentage() {
        val percentage = fileCount * 100 / listSize
        elementMonitor?.onSerializeProgress(percentage)
    }
}


interface ElementSerializeMonitor {
    fun onSerializeProgress(percentage: Int)
}