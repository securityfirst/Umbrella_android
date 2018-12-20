package org.secfirst.umbrella.whitelabel.data.disk

import org.eclipse.jgit.api.Git
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import java.io.File

class TentConfig {
    companion object {
        private val repoPath = UmbrellaApplication.instance.cacheDir.path + "/repo/"
        const val BRANCH_NAME: String = "refs/heads/master"
        var uriRepository = "https://github.com/douglasalipio/umbrella-content"
        const val ELEMENT_LEVEL = 2
        const val SUB_ELEMENT_LEVEL = 3
        const val CHILD_LEVEL = 4
        fun getDelimiter(fileName: String) = if (fileName == TypeFile.CATEGORY.value) fileName
        else fileName.substringBeforeLast("_")

        fun isRepCreate() = File(repoPath).exists()

        fun isNotRepCreate() = !File(repoPath).exists()

        fun getPathRepository(): String = repoPath

        fun extensionFile(absolutePath: String) = absolutePath.substringAfterLast(".")
    }
}

enum class TypeFile(val value: String) {
    CHECKLIST("c"),
    FORM("f"),
    CATEGORY(".category"),
    IMG_CATEGORY("png"),
    SEGMENT("s"),
    NOUN("")
}

enum class ExtensionFile(val value: String) {
    YML("yml"),
    MD("md"),
    PNG("png"),
}

fun String.shortName(): String {
    val fullName = this.substringAfterLast("/")
    val fileName = fullName.substringBeforeLast(".")
    return fileName.substringBeforeLast("_")
}

fun String.nameWithoutExtension(): String {
    val fullName = this.substringAfterLast("/")
    return fullName.substringBeforeLast(".")
}

fun getLastCommitID() {
    val git = Git.open(File("${TentConfig.getPathRepository()}/.git"))
    val head = git.repository.getRef("HEAD")
    println("Ref of HEAD: " + head + ": " + head.name + " - " + head.objectId.name)
}
