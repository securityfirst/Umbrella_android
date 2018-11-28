import Extensions.Companion.PERMISSION_REQUEST_EXTERNAL_STORAGE
import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.text.format.DateFormat
import android.util.Log
import com.jakewharton.processphoenix.ProcessPhoenix
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity
import java.io.*

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

fun saveHtmlFile(html : String?) {

    val path = Environment.getExternalStorageDirectory().path
    var fileName = DateFormat.format("dd_MM_yyyy_hh_mm_ss", System.currentTimeMillis()).toString()
    fileName += ".html"
    val file = File(path, fileName)
    try {
        val out = FileOutputStream(file)
        val data = html?.toByteArray()
        out.write(data)
        out.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

fun MainActivity.requestExternalStoragePermission() {

    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_EXTERNAL_STORAGE)
    } else {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_EXTERNAL_STORAGE)
    }
}

fun doRestartApplication(context: Context) {
    val tourIntent = Intent(context, MainActivity::class.java)
    tourIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    ProcessPhoenix.triggerRebirth(context, tourIntent)
}

fun getAssetFileBy(fileName: String) = UmbrellaApplication.instance.assets.open(fileName)

