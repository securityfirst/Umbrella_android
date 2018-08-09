package org.secfirst.umbrella.whitelabel.data.storage

import java.io.File

class TentConfig(private val repoPath: String) {

    companion object {
        const val BRANCH_NAME: String = "refs/heads/master"
        const val URI_REPOSITORY = "https://github.com/douglasalipio/umbrella-content"
        const val FORM_NAME = "forms"
        const val ELEMENT_LEVEL = 1
        const val SUB_ELEMENT_LEVEL = 2
        const val CHILD_LEVEL = 3
        fun getDelimiter(fileName: String): String {
            return if (fileName == TypeFile.CATEGORY.value)
                fileName
            else
                fileName.substringBeforeLast("_")
        }
    }

    fun isCreate() = File(repoPath).exists()
    fun isNotCreate() = !File(repoPath).exists()
    fun getPathRepository(): String = repoPath

}

enum class TypeFile(val value: String) {
    CHECKLIST("c"),
    FORM("f"),
    CATEGORY(".category"),
    SEGMENT("s"),
    NOUN("")
}

enum class ExtensionFile(val value: String) {
    YML("yml"),
    MD("md")
}