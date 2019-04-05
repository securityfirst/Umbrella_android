package org.secfirst.umbrella.whitelabel.serialize

import android.util.Log
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.removeHead
import org.secfirst.umbrella.whitelabel.data.database.segment.replaceMarkdownImage
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.feature.content.ContentService

import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.deviceLanguage
import org.secfirst.umbrella.whitelabel.misc.parseYmlFile
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject


class ElementLoader @Inject constructor(private val tentRepo: TentRepo, contentService: ContentService? = null) {

    private val root: Root = Root()
    private val elementMonitor: ElementSerializeMonitor? = contentService
    private var fileCount = 0
    private var listSize = 0

    suspend fun process(): Root {
        withContext(ioContext) {
            val files = tentRepo.loadElementsFile()
            listSize = files.size
            files.forEach { pairFile ->
                val currentFile = pairFile.second
                val absolutePath = currentFile.path
                        .substringAfterLast(PathUtils.basePath(), "")
                val pwd = getWorkDirectory(absolutePath)
                Log.d("test", "id - $absolutePath")
                serializeCategories(pwd, pairFile)
                calculatePercentage()
            }
            val filesPair = tentRepo.loadFile()
            filesPair.forEach { serializeFiles(it) }

        }
        return root
    }

    private fun calculatePercentage() {
        val percentage = fileCount * 50 / listSize
        elementMonitor?.onSerializeProgress(percentage)
    }

    private fun serializeFiles(pairFile: Pair<String, File>) {
        val file = pairFile.second
        val id = pairFile.first
        val absolutePath = file.path.substringAfterLast("${PathUtils.basePath()}/${defaultTentLanguage()}/", "")
        val pwd = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")

        when (PathUtils.getLevelOfPath(pwd)) {
            ELEMENT_LEVEL -> {
                root.elements.forEach {
                    if (it.path == pwd) {
                        when (file.nameWithoutExtension.substringBeforeLast("_")) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                it.markdowns.add(Markdown(id, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val newChecklist = parseYmlFile(file, Checklist::class)
                                newChecklist.id = id
                                it.checklist.add(newChecklist)
                            }
                        }
                    }
                }
            }
            SUB_ELEMENT_LEVEL -> {
                root.elements.walkSubElement {
                    if (it.path == pwd) {
                        when (file.extension) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                it.markdowns.add(Markdown(id, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val checklist = parseYmlFile(file, Checklist::class)
                                checklist.id = id
                                it.checklist.add(checklist)
                            }
                        }
                    }
                }
            }
            CHILD_LEVEL -> {
                root.elements.walkChild {
                    if (it.path == pwd) {
                        when (file.extension) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                it.markdowns.add(Markdown(id, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val newChecklist = parseYmlFile(file, Checklist::class)
                                newChecklist.id = id
                                it.checklist.add(newChecklist)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun serializeCategories(pwd: String, pairFile: Pair<String, File>) {
        val currentFile = pairFile.second
        val element = parseYmlFile(currentFile, Element::class)
        element.path = pwd
        element.pathId = pairFile.first
        element.resourcePath = element.icon.filterImageCategoryFile()
        element.rootDir = PathUtils.getLastDirectory(pwd)
        when (PathUtils.getLevelOfPath(element.path)) {
            ELEMENT_LEVEL -> {
                root.elements.add(element)
                fileCount++
            }
            SUB_ELEMENT_LEVEL -> {
                root.elements.last().children.add(element)
                fileCount++
            }
            else -> {
                root.elements.last().children.last().children.add(element)
                fileCount++
            }
        }
    }
}

interface ElementSerializeMonitor {
    fun onSerializeProgress(percentage: Int)
}