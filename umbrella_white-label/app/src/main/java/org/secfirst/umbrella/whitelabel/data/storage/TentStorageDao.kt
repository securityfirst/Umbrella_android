package org.secfirst.umbrella.whitelabel.data.storage

import android.util.Log
import kotlinx.coroutines.experimental.withContext
import org.eclipse.jgit.api.Git
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import java.io.File
import java.util.*

interface TentStorageDao {

    suspend fun cloneRepository(tentConfig: TentConfig): Boolean {
        var result = true
        try {
            withContext(ioContext) {
                if (tentConfig.isNotCreate()) {
                    Git.cloneRepository()
                            .setURI(TentConfig.URI_REPOSITORY)
                            .setDirectory(File(tentConfig.getPathRepository()))
                            .setBranchesToClone(Arrays.asList(TentConfig.BRANCH_NAME))
                            .setBranch(TentConfig.BRANCH_NAME)
                            .call()
                }
            }
        } catch (e: Exception) {
            result = false
            File(tentConfig.getPathRepository()).deleteRecursively()
            Log.i(TentStorageDao::class.java.name,
                    "Repository wasn't created - ${tentConfig.isNotCreate()} " +
                            "path - ${tentConfig.getPathRepository()}")
        }

        return result
    }


    suspend fun filterByElement(tentConfig: TentConfig): List<File> {
        val files: MutableList<File> = arrayListOf()
        withContext(ioContext) {
            File(tentConfig.getPathRepository())
                    .walk()
                    .filter { file -> !file.path.contains(".git") }
                    .filter { file ->
                        TentConfig.getDelimiter(file.name) == TypeFile.SEGMENT.value
                                || TentConfig.getDelimiter(file.name) == TypeFile.CHECKLIST.value
                                || TentConfig.getDelimiter(file.name) == TypeFile.FORM.value
                    }
                    .filter { it.isFile }
                    .forEach { file -> files.add(file) }
            files.reverse()
        }
        return files
    }

    suspend fun filterBySubElement(tentConfig: TentConfig): List<File> {
        val files: MutableList<File> = arrayListOf()
        withContext(ioContext) {
            File(tentConfig.getPathRepository())
                    .walk()
                    .filter { file -> !file.path.contains(".git") }
                    .filter { file -> file.name == ".category.yml" }
                    .filter { it.isFile }
                    .forEach { file -> files.add(file) }
            files.reverse()
        }
        return files
    }
}