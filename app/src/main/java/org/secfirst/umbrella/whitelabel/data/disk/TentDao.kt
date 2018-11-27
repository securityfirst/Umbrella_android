package org.secfirst.umbrella.whitelabel.data.disk

import android.util.Log
import kotlinx.coroutines.experimental.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.util.io.DisabledOutputStream
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig.Companion.BRANCH_NAME
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import java.io.File
import java.util.*


interface TentDao {

    suspend fun cloneRepository(tentConfig: TentConfig): Boolean {
        var result = true
        try {
            withContext(ioContext) {
                if (tentConfig.isNotRepCreate()) {
                    Git.cloneRepository()
                            .setURI(TentConfig.uriRepository)
                            .setDirectory(File(tentConfig.getPathRepository()))
                            .setBranchesToClone(Arrays.asList(TentConfig.BRANCH_NAME))
                            .setBranch(TentConfig.BRANCH_NAME)
                            .call()
                }
            }
        } catch (e: Exception) {
            result = false
            File(tentConfig.getPathRepository()).deleteRecursively()
            Log.i(TentDao::class.java.name,
                    "Repository wasn't created - ${tentConfig.isNotRepCreate()} " +
                            "path - ${tentConfig.getPathRepository()}")
        }

        return result
    }

    suspend fun rebaseBranch(tentConfig: TentConfig): Boolean {
        var result = true
        val git = Git.open(File("${tentConfig.getPathRepository()}/.git"))
        try {
            withContext(ioContext) {
                git.checkout().setName("master").call()
                val branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
                branches.forEach { branch ->
                    if (BRANCH_NAME == branch.name)
                        git.pull().setRemoteBranchName("master").setRebase(true).call()
                }
            }
        } catch (e: Exception) {
            result = false
            // File(tentConfig.getPathRepository()).deleteRecursively()
        }

        return result
    }

    private fun filesChanged(git: Git) {
        val reader = git.repository.newObjectReader()
        val oldTreeIter = CanonicalTreeParser()
        val oldTree = git.repository.resolve("HEAD~1^{tree}")
        oldTreeIter.reset(reader, oldTree)
        val newTreeIter = CanonicalTreeParser()
        val newTree = git.repository.resolve("HEAD^{tree}")
        newTreeIter.reset(reader, newTree)

        val diffFormatter = DiffFormatter(DisabledOutputStream.INSTANCE)
        diffFormatter.setRepository(git.repository)
        val entries = diffFormatter.scan(oldTreeIter, newTreeIter)

        for (entry in entries) {
            println(entry.changeType)
        }
    }

    fun filterByElement(tentConfig: TentConfig): List<Pair<String, File>> {
        val files = mutableListOf<Pair<String, File>>()
        val entry = getDiffEntry(tentConfig)

        entry.forEach { diffEntry ->
            val fileName = diffEntry.newPath.nameWithoutExtension().shortName()
            val absoluteFilePath = tentConfig.getPathRepository() + diffEntry.newPath
            println(diffEntry.newPath)
            if (fileName == TypeFile.SEGMENT.value ||
                    fileName == TypeFile.CHECKLIST.value ||
                    fileName == TypeFile.FORM.value ||
                    fileName == TypeFile.IMG_CATEGORY.value)
                files.add(Pair(diffEntry.newId.name(), File(absoluteFilePath)))
        }
        return files.toList()
    }

    suspend fun filterCategoryFiles(tentConfig: TentConfig): List<Pair<String, File>> {
        val files = mutableListOf<Pair<String, File>>()
        withContext(ioContext) {
            val entry = getDiffEntry(tentConfig)
            entry.forEach { diffEntry ->
                val fileName = diffEntry.newPath.nameWithoutExtension()
                val absoluteFilePath = tentConfig.getPathRepository() + diffEntry.newPath
                if (fileName == TypeFile.CATEGORY.value)
                    files.add(Pair(diffEntry.newId.name(), File(absoluteFilePath)))
            }
        }
        return files.toList()
    }

    fun filterImageCategoryFile(imgName: String, tentConfig: TentConfig): String = File(tentConfig.getPathRepository())
            .walk()
            .filter { file -> !file.path.contains(".git") }
            .filter { file -> file.name == imgName }
            .filter { it.isFile }
            .last()
            .path

    private fun getDiffEntry(tentConfig: TentConfig): List<DiffEntry> {
        val git = Git.open(File("${tentConfig.getPathRepository()}/.git"))
        val reader = git.repository.newObjectReader()
        val newTreeIterator = CanonicalTreeParser()
        val newTree = git.repository.resolve("HEAD^{tree}")
        newTreeIterator.reset(reader, newTree)
        val diffFormatter = DiffFormatter(DisabledOutputStream.INSTANCE)
        diffFormatter.setRepository(git.repository)
        return diffFormatter.scan(null, newTree)
    }

}