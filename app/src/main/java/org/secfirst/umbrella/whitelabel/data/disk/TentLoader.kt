package org.secfirst.umbrella.whitelabel.data.disk

import android.util.Log
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
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

    private val root: Root = Root()
    private val elementMonitor: ElementSerializeMonitor? = contentService
    private var fileCount = 0
    private var listSize = 0

    suspend fun process(): Root {
        withContext(ioContext) {
            val categoriesFiles = tentRepo.loadElementsFile()
            val contentFiles = tentRepo.loadFile()
            listSize = categoriesFiles.size + contentFiles.size
            categoriesFiles.forEach {
                fileCount++
                calculatePercentage()
                serializeCategories(it)
            }
            contentFiles.forEach {
                fileCount++
                serializeFiles(it)
                calculatePercentage()
            }
        }
        return root
    }

    private fun calculatePercentage() {
        val percentage = fileCount * 100 / listSize
        elementMonitor?.onSerializeProgress(percentage)
    }

    private fun serializeFiles(file: File) {
        val absolutePath = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")
        val pwd = absolutePath.substringBeforeLast("/${file.name}")
        val elements = root.elements.filter { it.rootDir == pwd }
        if (elements.isNotEmpty()) {
            loadContent(elements.last(), file)
        } else {
            root.elements.forEach { element ->
                val subElements = element.children.filter { it.rootDir == pwd }
                if (subElements.isNotEmpty())
                    loadContent(subElements.last(), file)
                else {
                    subElements.forEach { child ->
                        val children = child.children.filter { it.rootDir == pwd }
                        if (children.isNotEmpty())
                            loadContent(children.last(), file)
                    }
                }
            }
        }
    }

    private fun loadContent(element: Element, file: File) {
        val absolutePath = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")
        when (file.nameWithoutExtension.substringBeforeLast("_")) {
            TypeFile.SEGMENT.value -> {
                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                element.markdowns.add(Markdown(file.path, markdownFormatted).removeHead())
                Log.d("test", "id - ${element.path}")
            }
            TypeFile.CHECKLIST.value -> {
                val newChecklist = parseYmlFile(file, Checklist::class)
                newChecklist.id = file.path
                element.checklist.add(newChecklist)
                Log.d("test", "id - ${element.path}")
            }
        }
    }

    private fun serializeCategories(file: File) {
        val element = parseYmlFile(file, Element::class)
        val absolutePath = file.path.substringAfterLast("${getPathRepository()}${deviceLanguage()}/")
        val pwd = absolutePath.substringBeforeLast("/${file.name}")
        element.pathId = file.path
        element.path = file.path
        element.resourcePath = element.icon.filterImageCategoryFile()
        element.rootDir = pwd

        when (getLevelOfPath(absolutePath)) {
            ELEMENT_LEVEL -> {
                root.elements.add(element)
                Log.d("test", "id - ${element.path}")
            }
            SUB_ELEMENT_LEVEL -> {
                root.elements.last().children.add(element)
                Log.d("test", "id - ${element.path}")
            }
            else -> {
                root.elements.last().children.last().children.add(element)
                Log.d("test", "id - ${element.path}")
            }
        }
    }
}

interface ElementSerializeMonitor {
    fun onSerializeProgress(percentage: Int)
}