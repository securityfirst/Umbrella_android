import Extensions.Companion.PERMISSION_REQUEST_EXTERNAL_STORAGE
import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Base64
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jakewharton.processphoenix.ProcessPhoenix
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity
import java.io.File
import java.util.*
import kotlin.reflect.KClass


class Extensions {
    companion object {
        const val PERMISSION_REQUEST_EXTERNAL_STORAGE = 1
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

fun <T : Any> parseYmlFile(file: File, c: KClass<T>): T {
    val mapper = ObjectMapper(YAMLFactory())
    mapper.registerModule(KotlinModule())
    return file.bufferedReader().use { mapper.readValue(it.readText(), c.java) }
}

fun setMaskMode(activity: Activity, masked: Boolean) {
    val packageName = BuildConfig.APPLICATION_ID
    val disableNames = ArrayList<String>()
    disableNames.add("$packageName.MainActivityNormal")
    disableNames.add("$packageName.MainActivityCalculator")
    val activeName = disableNames.removeAt(if (masked) 1 else 0)

    activity.packageManager.setComponentEnabledSetting(
            ComponentName(packageName, activeName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

    for (i in disableNames.indices) {
        activity.packageManager.setComponentEnabledSetting(
                ComponentName(packageName, disableNames[i]),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }
}

fun encodeToBase64(file: File) = Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT)