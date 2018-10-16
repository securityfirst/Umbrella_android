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
import org.secfirst.umbrella.whitelabel.data.database.segment.replaceMarkdownImage
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getLevelOfPath
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject

class ElementLoader @Inject constructor(private val tentRepo: TentRepo) : Serializer {

    private lateinit var root: Root

    suspend fun load(root: Root): Root {
        withContext(ioContext) {
            this.root = root
            tentRepo.loadFile().filter { it.extension != ExtensionFile.PNG.value }.forEach { currentFile ->
                val absolutePath = currentFile.path.substringAfterLast(PathUtils.basePath(), "")
                val pwd = getWorkDirectory(absolutePath)
                loadElement(pwd, currentFile)
                loadForm(currentFile)
            }
        }
        return this.root
    }

    private fun loadElement(pwd: String, file: File) {

        when (getLevelOfPath(pwd)) {
            ELEMENT_LEVEL -> {
                root.elements.forEach {
                    if (it.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(pwd)
                                it.markdowns.add(Markdown(markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> it.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }
            SUB_ELEMENT_LEVEL -> {
                root.elements.walkSubElement { subElement ->
                    if (subElement.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(pwd)
                                subElement.markdowns.add(Markdown(markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> subElement.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }
            CHILD_LEVEL -> {
                root.elements.walkChild { child ->
                    if (child.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(pwd)
                                child.markdowns.add(Markdown(markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> child.checklist.add(parseYmlFile(file, Checklist::class))
                        }
                    }
                }
            }
        }
    }

    private fun loadForm(file: File) {
        if (getDelimiter(file.nameWithoutExtension) == TypeFile.FORM.value)
            root.forms.add(parseYmlFile(file, Form::class))
    }
}