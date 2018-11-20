import Extensions.Companion.PERMISSION_REQUEST_EXTERNAL_STORAGE
import android.Manifest
import android.content.Context
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity
import java.io.*
import java.nio.channels.FileChannel


class Extensions {
    companion object {

        const val PERMISSION_REQUEST_EXTERNAL_STORAGE = 1

        @Throws(IOException::class)
        fun copyFile(context: Context) {
            val outroPath = context.getDatabasePath(AppDatabase.NAME + ".db")
            var fis: FileInputStream? = null
            var fos: FileOutputStream? = null

            try {
                fis = FileInputStream(outroPath)
                val path = Environment.getExternalStorageDirectory()
                fos = FileOutputStream("$path/db_dump.db")
                while (true) {
                    val i = fis.read()
                    if (i != -1) {
                        fos.write(i)
                    } else {
                        break
                    }
                }
                fos.flush()
                Log.i("test", "DB dump OK")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("test", "DB dump ERROR")
            } finally {
                try {
                    fos?.close()
                    fis?.close()
                } catch (ioe: IOException) {
                }

            }
        }
    }
}

fun requestExternalStoragePermission(mainActivity: MainActivity) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        ActivityCompat.requestPermissions(mainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_EXTERNAL_STORAGE)
    } else {
        ActivityCompat.requestPermissions(mainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_EXTERNAL_STORAGE)
    }
}

@Throws(IOException::class)
fun copyFile(src: File, dst: File) {
    val inStream = FileInputStream(src)
    val outStream = FileOutputStream(dst)
    val inChannel = inStream.channel
    val outChannel = outStream.channel
    inChannel.transferTo(0, inChannel.size(), outChannel)
    inStream.close()
    outStream.close()
}

@Throws(IOException::class)
fun copyFilee(sourceFile: File, destFile: File) {
    if (!destFile.parentFile.exists())
        destFile.parentFile.mkdirs()

    if (!destFile.exists()) {
        destFile.createNewFile()
    }

    var source: FileChannel? = null
    var destination: FileChannel? = null

    try {
        source = FileInputStream(sourceFile).channel
        destination = FileOutputStream(destFile).channel
        destination!!.transferFrom(source, 0, source!!.size())
    } finally {
        if (source != null) {
            source!!.close()
        }
        if (destination != null) {
            destination!!.close()
        }
    }
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    inputStream.use { input ->
        this.outputStream().use { fileOut ->
            input.copyTo(fileOut)
        }
    }
}


fun getAssetFileBy(fileName: String) = UmbrellaApplication.instance.assets.open(fileName)

