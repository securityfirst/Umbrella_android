package org.secfirst.umbrella.whitelabel.serialize

import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.removeHead
import org.secfirst.umbrella.whitelabel.data.database.segment.replaceMarkdownImage
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.CHILD_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.SUB_ELEMENT_LEVEL
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.getDelimiter
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getLevelOfPath
import org.secfirst.umbrella.whitelabel.serialize.PathUtils.Companion.getWorkDirectory
import parseYmlFile
import java.io.File
import javax.inject.Inject

class ElementLoader @Inject constructor(private val tentRepo: TentRepo) {


    suspend fun load(root: Root): Root {
        withContext(ioContext) {
            val filesPair = tentRepo.loadFile()
            filesPair.filter { it.second.extension != ExtensionFile.PNG.value }.forEach { pairFile ->
                val file = pairFile.second
                val absolutePath = file.path.substringAfterLast(PathUtils.basePath(), "")
                val pwd = getWorkDirectory(absolutePath)
                loadElement(pwd, pairFile, absolutePath, root)
                loadForm(pairFile, root)
            }
        }
        return root
    }

    private fun loadElement(pwd: String, pairFile: Pair<String, File>, absolutePath: String, root: Root) {
        val file = pairFile.second
        val pathId = pairFile.first

        when (getLevelOfPath(pwd)) {
            ELEMENT_LEVEL -> {
                root.elements.forEach {
                    if (it.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                it.markdowns.add(Markdown(pathId, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val newChecklist = parseYmlFile(file, Checklist::class)
                                newChecklist.id = pathId
                                it.checklist.add(newChecklist)
                            }
                        }
                    }
                }
            }
            SUB_ELEMENT_LEVEL -> {
                root.elements.walkSubElement { subElement ->
                    if (subElement.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                subElement.markdowns.add(Markdown(pathId, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val checklist = parseYmlFile(file, Checklist::class)
                                checklist.id = pathId
                                subElement.checklist.add(checklist)
                            }
                        }
                    }
                }
            }
            CHILD_LEVEL -> {
                root.elements.walkChild { child ->
                    if (child.path == pwd) {
                        when (getDelimiter(file.nameWithoutExtension)) {
                            TypeFile.SEGMENT.value -> {
                                val markdownFormatted = file.readText().replaceMarkdownImage(absolutePath)
                                child.markdowns.add(Markdown(pathId, markdownFormatted).removeHead())
                            }
                            TypeFile.CHECKLIST.value -> {
                                val newChecklist = parseYmlFile(file, Checklist::class)
                                newChecklist.id = pathId
                                child.checklist.add(newChecklist)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadForm(pairFile: Pair<String, File>, root: Root) {
        val file = pairFile.second
        val sha1ID = pairFile.first
        if (getDelimiter(file.nameWithoutExtension) == TypeFile.FORM.value) {
            val form = parseYmlFile(file, Form::class)
            form.path = sha1ID
            root.forms.add(form)
        }
    }
}