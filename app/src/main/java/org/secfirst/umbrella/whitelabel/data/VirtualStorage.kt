package org.secfirst.umbrella.whitelabel.data

import android.app.Application
import info.guardianproject.iocipher.VirtualFileSystem
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject


class VirtualStorage @Inject constructor(private val application: Application) {

    suspend fun mountFilesystem(document: String, fileName: String): File {
        val file = File(application.cacheDir, "$fileName.html")
        withContext(AppExecutors.ioContext) {
            if (!file.exists()) file.createNewFile()
            val virtualSystem = VirtualFileSystem.get()
            virtualSystem.containerPath = file.path
            if (!virtualSystem.isMounted) virtualSystem.mount("password")
            if (!virtualSystem.isMounted) virtualSystem.createNewContainer(file.path, "password")
        }
        return write(file, document)
    }

    private fun write(file: File, document: String): File {
        val writer = BufferedWriter(FileWriter(file))
        writer.write(document)
        writer.flush()
        writer.close()
        return file
    }
}