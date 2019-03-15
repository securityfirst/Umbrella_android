package org.secfirst.umbrella.whitelabel.serialize

import android.util.Log
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.disk.*

import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.parseYmlFile
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject


class ElementSerialize @Inject constructor(private val tentRepo: TentRepo, contentService: ContentService? = null) {

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
                addElement(pwd, pairFile)
                calculatePercentage()
            }
        }
        return root
    }

    private fun addElement(pwd: String, pairFile: Pair<String, File>) {
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

    private fun calculatePercentage() {
        val percentage = fileCount * 50 / listSize
        elementMonitor?.onSerializeProgress(percentage)
    }
}

interface ElementSerializeMonitor {
    fun onSerializeProgress(percentage: Int)
}
