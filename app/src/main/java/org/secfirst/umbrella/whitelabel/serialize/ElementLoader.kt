package org.secfirst.umbrella.whitelabel.serialize

import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.removeHead
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.CHILD_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.SUB_ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.getDelimiter
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.replaceMarkdownImage
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getLevelOfPath
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject

class ElementLoader @Inject constructor(private val tentRepo: TentRepo) : Serializer {

    private lateinit var root: Root

    suspend fun load(pRoot: Root): Root {
        withContext(ioContext) {
            root = pRoot
            val files = tentRepo.loadFile()
            create(files)
        }
        return pRoot
    }

    private fun create(files: List<File>) {
        files.forEach { currentFile ->
            val absolutePath = currentFile.path.substringAfterLast(PathUtils.basePath(), "")
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
                            TypeFile.SEGMENT.value -> it.markdowns.add(Markdown(file.readText().replaceMarkdownImage(pwd)).removeHead())
                            TypeFile.CHECKLIST.value -> it.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }

            SUB_ELEMENT_LEVEL -> {
                root.elements.walkSubElement { subElement ->
                    if (subElement.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> subElement.markdowns.add(Markdown(file.readText().replaceMarkdownImage(pwd)).removeHead())
                            TypeFile.CHECKLIST.value -> subElement.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }

            CHILD_LEVEL -> {
                root.elements.walkChild { child ->
                    if (child.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> child.markdowns.add(Markdown(file.readText().replaceMarkdownImage(pwd)).removeHead())
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