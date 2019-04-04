package org.secfirst.umbrella.whitelabel.serialize

import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.removeHead
import org.secfirst.umbrella.whitelabel.data.database.segment.replaceMarkdownImage
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.parseYmlFile
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getLevelOfPath
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import java.io.File
import javax.inject.Inject

class ElementLoader @Inject constructor(private val tentRepo: TentRepo) {

    suspend fun load(root: Root): Root {
        withContext(ioContext) {
            val filesPair = tentRepo.loadFile()
            filesPair
                    .filter { it.second.extension != ExtensionFile.PNG.value }
                    .forEach {
                        loadElement(it, root)
                    }

            loadForm(root)
        }
        return root
    }

    private fun loadElement(pairFile: Pair<String, File>, root: Root) {

        val file = pairFile.second
        val id = pairFile.first
        val absolutePath = file.path.substringAfterLast(PathUtils.basePath(), "")
        val pwd = getWorkDirectory(absolutePath)

        when (getLevelOfPath(pwd)) {
            ELEMENT_LEVEL -> {
                root.elements.forEach {
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
            SUB_ELEMENT_LEVEL -> {
                root.elements.walkSubElement { subElement ->
                    if (subElement.path == pwd) {
                        when (file.extension) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                subElement.markdowns.add(Markdown(id, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val checklist = parseYmlFile(file, Checklist::class)
                                checklist.id = id
                                subElement.checklist.add(checklist)
                            }
                        }
                    }
                }
            }
            CHILD_LEVEL -> {
                root.elements.walkChild { child ->
                    if (child.path == pwd) {
                        when (file.extension) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                child.markdowns.add(Markdown(id, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val newChecklist = parseYmlFile(file, Checklist::class)
                                newChecklist.id = id
                                child.checklist.add(newChecklist)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadForm(root: Root) {
        tentRepo.loadFormFile().forEach {
            val file = it.second
            val id = it.first
            val form = parseYmlFile(file, Form::class)
            form.path = id
            form.deeplinkTitle = form.title.toLowerCase()
            root.forms.add(form)
        }
    }
}