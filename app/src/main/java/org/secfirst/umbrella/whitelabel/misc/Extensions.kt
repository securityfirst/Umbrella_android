import android.content.Context
import android.os.Environment
import android.util.Log
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class Extensions {
    companion object {
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

fun getAssetFileBy(fileName: String) = UmbrellaApplication.instance.assets.open(fileName)

