package org.secfirst.umbrella.whitelabel.misc

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.system.Os.read
import android.util.Log
import android.webkit.MimeTypeMap

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

import info.guardianproject.iocipher.File
import info.guardianproject.iocipher.FileInputStream
import info.guardianproject.iocipher.VirtualFileSystem
import org.secfirst.umbrella.whitelabel.UmbrellaApplication

import java.util.Arrays.copyOf

class IOCipherContentProvider : ContentProvider() {
    private var mimeTypeMap: MimeTypeMap? = null

    override fun onCreate(): Boolean {
        mimeTypeMap = MimeTypeMap.getSingleton()
        return true
    }

    override fun getType(uri: Uri): String? {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return mimeTypeMap!!.getMimeTypeFromExtension(fileExtension)
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        var pipe: Array<ParcelFileDescriptor>? = null
        var `in`: InputStream? = null

        try {
            pipe = ParcelFileDescriptor.createPipe()
            val path = uri.path
            Log.i(TAG, "streaming $path")
            // BufferedInputStream could help, AutoCloseOutputStream conflicts
            `in` = FileInputStream(File(path))
            PipeFeederThread(`in`, ParcelFileDescriptor.AutoCloseOutputStream(pipe!![1])).start()
        } catch (e: IOException) {
            Log.e(TAG, "Error opening pipe", e)
            throw FileNotFoundException("Could not open pipe for: " + uri.toString())
        }

        return pipe[0]
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
                       sortOrder: String?): Cursor? {
        var projection = projection
        val f = java.io.File(UmbrellaApplication.instance.cacheDir, SHARE_FILE)
        if (!f.exists()) {
            throw RuntimeException("Operation not supported")
        }
        val path = f.getPath()

        val vfs = VirtualFileSystem.get()
        vfs.containerPath = path
        if (!vfs.isMounted) vfs.mount(path)

        val file = File("/", uri.path.replace(FILES_URI.path, ""))

        if (projection == null) {
            projection = COLUMNS
        }

        var cols = arrayOfNulls<String>(projection.size)
        var values = arrayOfNulls<Any>(projection.size)
        var i = 0
        for (col in projection) {
            if (OpenableColumns.DISPLAY_NAME == col) {
                cols[i] = OpenableColumns.DISPLAY_NAME
                values[i++] = file.name
            } else if (OpenableColumns.SIZE == col) {
                cols[i] = OpenableColumns.SIZE
                values[i++] = file.length()
            }
        }

        cols = copyOf<String>(cols, i)
        values = copyOf<Any>(values, i)

        val cursor = MatrixCursor(cols, 1)
        cursor.addRow(values)
        return cursor
    }

    override fun insert(uri: Uri, initialValues: ContentValues?): Uri? {
        throw RuntimeException("Operation not supported")
    }

    override fun update(uri: Uri, values: ContentValues?, where: String?,
                        whereArgs: Array<String>?): Int {
        throw RuntimeException("Operation not supported")
    }

    override fun delete(uri: Uri, where: String?, whereArgs: Array<String>?): Int {
        throw RuntimeException("Operation not supported")
    }

    internal class PipeFeederThread(var input: InputStream, private var out: OutputStream) : Thread() {

        override fun run() {
            val buf = ByteArray(8192)
            var len: Int = input.read(buf)

            try {
                while (len > 0) {
                    out.write(buf, 0, len)
                }

                input.close()
                out.flush()
                out.close()
            } catch (e: IOException) {
                Log.e(TAG, "File transfer failed:", e)
            }

        }
    }

    companion object {
        val TAG = "IOCipherContentProvider"
        val SHARE_FILE = "share.db"
        val FILES_URI = Uri
                .parse("content://org.secfirst.umbrella/")
        private val COLUMNS = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)
    }
}