package org.secfirst.umbrella.whitelabel.serialize

import android.util.Log
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.Element
import org.secfirst.umbrella.whitelabel.data.Root
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig
import org.secfirst.umbrella.whitelabel.data.disk.TentRepo
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject


class ElementSerializer @Inject constructor(private val tentRepo: TentRepo) : Serializer {

    private val root: Root = Root()
    private var fileList = listOf<File>()

    suspend fun serialize(): Root {
        withContext(ioContext) {
            fileList = tentRepo.loadElementsFile()
            create()
        }

        return root
    }

    private fun create() {
        fileList.forEach { currentFile ->
            val absolutePath = currentFile.path
                    .substringAfterLast("en/", "")
            val pwd = getWorkDirectory(absolutePath)
            Log.e("test", "path - $absolutePath")
            addElement(pwd, currentFile)
        }
    }

    private fun addElement(pwd: String, currentFile: File) {
        val element = parseYmlFile(currentFile, Element::class)
        element.path = pwd
        element.resourcePath = if (element.icon.isNotEmpty()) tentRepo.loadCategoryImage(element.icon) else ""
        element.rootDir = PathUtils.getLastDirectory(pwd)

        when (PathUtils.getLevelOfPath(element.path)) {
            TentConfig.ELEMENT_LEVEL -> root.elements.add(element)
            TentConfig.SUB_ELEMENT_LEVEL -> root.elements.last().children.add(element)
            else -> root.elements.last().children.last().children.add(element)
        }
    }
}