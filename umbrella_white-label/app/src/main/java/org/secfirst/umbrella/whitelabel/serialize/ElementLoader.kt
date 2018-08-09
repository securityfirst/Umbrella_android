package org.secfirst.umbrella.whitelabel.serialize

import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.*
import org.secfirst.umbrella.whitelabel.data.storage.TentConfig.Companion.CHILD_LEVEL
import org.secfirst.umbrella.whitelabel.data.storage.TentConfig.Companion.ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.storage.TentConfig.Companion.SUB_ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.storage.TentConfig.Companion.getDelimiter
import org.secfirst.umbrella.whitelabel.data.storage.TentStorageRepo
import org.secfirst.umbrella.whitelabel.data.storage.TypeFile
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getLevelOfPath
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject

class ElementLoader @Inject constructor(private val tentStorageRepo: TentStorageRepo) : Serializer {

    private var root = Root()
    private var files = listOf<File>()
    suspend fun load(pRoot: Root): Root {
        withContext(ioContext) {
            files = tentStorageRepo.getLoadersFile()
            root = pRoot
            create()
        }
        return root
    }

    private fun create() {
        files.forEach { currentFile ->
            val absolutePath = currentFile.path.substringAfterLast("en/", "")
            val pwd = getWorkDirectory(absolutePath)
            addProperties(pwd, currentFile)
            addForms(currentFile)
        }
    }

    private fun addProperties(pwd: String, file: File) {
        when (getLevelOfPath(pwd)) {
            ELEMENT_LEVEL -> {
                root.elements.forEach {
                    if (it.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> it.markdowns.add(Markdown(file.readText()))
                            TypeFile.CHECKLIST.value -> it.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }

            SUB_ELEMENT_LEVEL -> {
                root.elements.walkSubElement { subElement ->
                    if (subElement.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> subElement.markdowns.add(Markdown(file.readText()))
                            TypeFile.CHECKLIST.value -> subElement.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }

            CHILD_LEVEL -> {
                root.elements.walkChild { child ->
                    if (child.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> child.markdowns.add(Markdown(file.readText()))
                            TypeFile.CHECKLIST.value -> child.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }
        }
    }

    private fun addForms(file: File) {
        if (getDelimiter(file.nameWithoutExtension) == TypeFile.FORM.value)
            root.forms.add(parseYmlFile(file, Form::class))
    }
}