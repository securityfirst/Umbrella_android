package org.secfirst.umbrella.whitelabel.misc

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
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorker
import com.itextpdf.tool.xml.XMLWorkerHelper
import com.itextpdf.tool.xml.html.Tags
import com.itextpdf.tool.xml.parser.XMLParser
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext
import com.jakewharton.processphoenix.ProcessPhoenix
import org.apache.commons.io.FileUtils
import org.jsoup.select.Elements
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity
import java.io.*
import java.util.*
import kotlin.reflect.KClass


const val PERMISSION_REQUEST_EXTERNAL_STORAGE = 1

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

fun getAssetFileBy(fileName: String): InputStream = UmbrellaApplication.instance.assets.open(fileName)

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


fun htmlToPdf(doc: org.jsoup.nodes.Document, file: FileOutputStream) {
    //pdf
    val document = Document()
    val writer = PdfWriter.getInstance(document, file)
    document.open()
    // CSS
    val cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true)
    // HTML
    val htmlContext = HtmlPipelineContext(null)
    htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory())
    // Pipelines
    val pdf = PdfWriterPipeline(document, writer)
    val html = HtmlPipeline(htmlContext, pdf)
    val css = CssResolverPipeline(cssResolver, html)
    // XML Worker
    val worker = XMLWorker(css, true)
    val parser = XMLParser(worker)

    parser.parse(ByteArrayInputStream(doc.toString().toByteArray()))
    document.close()
    file.close()
}

fun createDocument(doc: org.jsoup.nodes.Document, filename: String, type: String, context: Context): File {
    val img: Elements = doc.getElementsByTag("img")
    lateinit var src: String
    lateinit var base64img: String

    img.forEach {
        src = it.absUrl("src").replace("file://", "")

        if (type == context.getString(R.string.html_name)) {
            base64img = encodeToBase64(File(src))
            src = "data:image/png;base64, $base64img"
        }
        it.attr("src", src)
    }

    val fileToShare = File(context.cacheDir, filename + ".${type.toLowerCase()}")

    when (type) {
        context.getString(R.string.html_name) -> {
            val writer = BufferedWriter(FileWriter(fileToShare))
            writer.write(doc.toString())
            writer.flush()
            writer.close()
        }
        context.getString(R.string.pdf_name) -> {
            htmlToPdf(doc, FileOutputStream(fileToShare))
        }
    }
    return fileToShare
}


fun encodeToBase64(file: File) = Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT)
        ?: ""

enum class TypeHelper(val value: String) {
    MODULE(Module::class.java.name),
    SUBJECT(Subject::class.java.name),
    DIFFICULTY(Difficulty::class.java.name)
}