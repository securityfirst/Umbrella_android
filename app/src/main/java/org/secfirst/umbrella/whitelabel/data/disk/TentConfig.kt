package org.secfirst.umbrella.whitelabel.data.disk

import java.io.File

class TentConfig(private val repoPath: String, private val resourcesPath: String) {

    companion object {
        const val BRANCH_NAME: String = "refs/heads/master"
        const val URI_REPOSITORY = "https://github.com/douglasalipio/umbrella-content"
        const val ELEMENT_LEVEL = 3
        const val SUB_ELEMENT_LEVEL = 4
        const val CHILD_LEVEL = 5
        fun getDelimiter(fileName: String) = if (fileName == TypeFile.CATEGORY.value) fileName
                                                   else fileName.substringBeforeLast("_")
    }

    fun isRepCreate() = File(repoPath).exists()
    fun isNotRepCreate() = !File(repoPath).exists()
    fun getPathRepository(): String = repoPath
    fun isResourceCreate() = File(resourcesPath).exists()
    fun isNotResourceCreate(fileName: String) = !File(resourcesPath + fileName).exists()

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
    PNG("png")
}