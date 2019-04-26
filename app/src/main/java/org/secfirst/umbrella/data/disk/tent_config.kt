package org.secfirst.umbrella.data.disk

import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.misc.deviceLanguage
import java.io.File


private val repoPath = UmbrellaApplication.instance.cacheDir.path + "/repo/"
const val BRANCH_NAME: String = "refs/heads/master"
const val baseUrlRepository = "https://github.com/securityfirst/umbrella-content"
const val ELEMENT_LEVEL = 2
const val SUB_ELEMENT_LEVEL = 3
const val CHILD_LEVEL = 4

fun getDelimiter(fileName: String) = if (fileName == TypeFile.CATEGORY.value) fileName
else fileName.substringBeforeLast("_")

fun isRepository() = File(repoPath).exists()

fun isNotRepository() = !File(repoPath).exists()

fun getPathRepository(): String = repoPath

fun defaultContent(): String = "en"

enum class TypeFile(val value: String) {
    CHECKLIST("c"),
    FORM("f"),
    CATEGORY(".category"),
    IMG_CATEGORY("png"),
    SEGMENT("s"),
    NOUN("")
}

enum class IsoCountry(val value: String) {
    ENGLISH("gb"),
    CHINESE("zh"),
    SPANISH("es"),
}

enum class Template(val value: String) {
    GLOSSARY("glossary")
}

enum class ExtensionFile(val value: String) {
    YML("yml"),
    MD("md"),
    PNG("png"),
}

fun tentLanguages(): List<String> {
    val languages = mutableListOf<String>()
    File(repoPath)
            .walk()
            .filter { !it.path.contains(".git") }
            .filter { it.name.length == 2 || it.name.contains("zh", true) }
            .filter { it.isDirectory }
            .forEach { languages.add(it.name) }
    return languages
}

fun defaultTentLanguage(): String {
    var value = defaultContent()
    tentLanguages().forEach { language ->
        if (language == deviceLanguage())
            value = deviceLanguage()
    }
    return value
}

fun getLastCommitID() {
    val git = Git.open(File("${getPathRepository()}/.git"))
    val head = git.repository.getRef("HEAD")
    println("Ref of HEAD: " + head + ": " + head.name + " - " + head.objectId.name)
}


suspend fun validateRepository(repositoryURL: String): Boolean {
    var result = false
    val repoUrl = "$repositoryURL.git"
    withContext(ioContext) {
        try {
            val db = FileRepositoryBuilder.create(File("/tmp"))
            val git = Git.wrap(db)
            val lsCmd = git.lsRemote()
            lsCmd.setRemote(repoUrl)
            if (null != lsCmd.call())
                result = true
        } catch (exception: Exception) {
            result = false
        }
    }
    return result
}

fun getLastDirectory(path: String): String {
    val splitPath = path.split("/").filter { it.isNotEmpty() }
    return splitPath[splitPath.lastIndex]
}

private fun getSplitPath(path: String) = path.split("/").filter { it.isNotEmpty() }

fun getLevelOfPath(path: String): Int {
    val pathSplitted = path.split("/").toMutableList()
    if (pathSplitted.size > 1) {
        pathSplitted.removeAt(pathSplitted.lastIndex)
    }
    return pathSplitted.size
}

fun getWorkDirectory(path: String): String {
    val splitPath = getSplitPath(path)
    var pwd = ""
    for (i in 1 until splitPath.size - 1)
        pwd += splitPath[i] + "/"
    return pwd
}

fun getWorkDirectoryFromImage(path: String): String {
    val splitPath = getSplitPath(path)
    var pwd = ""
    for (i in 0 until splitPath.size - 1)
        pwd += splitPath[i] + "/"
    return pwd
}

fun basePath(): String = UmbrellaApplication.instance.cacheDir.path