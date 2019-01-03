package org.secfirst.umbrella.whitelabel.serialize

import android.util.Log
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.parseYmlFile
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject


class ElementSerialize @Inject constructor(private val tentRepo: TentRepo) {

    private val root: Root = Root()

    suspend fun process(): Root {
        withContext(ioContext) {
            tentRepo.loadElementsFile().forEach { pairFile ->
                val currentFile = pairFile.second
                val absolutePath = currentFile.path
                        .substringAfterLast(PathUtils.basePath(), "")
                val pwd = getWorkDirectory(absolutePath)
                Log.d("test", "id - $absolutePath")
                addElement(pwd, pairFile)
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
            TentConfig.ELEMENT_LEVEL -> root.elements.add(element)
            TentConfig.SUB_ELEMENT_LEVEL -> root.elements.last().children.add(element)
            else -> root.elements.last().children.last().children.add(element)
        }
    }
}