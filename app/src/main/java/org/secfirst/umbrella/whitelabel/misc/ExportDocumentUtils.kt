package org.secfirst.umbrella.whitelabel.misc

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider
import android.support.v7.widget.AppCompatButton
import android.widget.RadioButton
import android.widget.RadioGroup
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerFontProvider
import com.itextpdf.tool.xml.XMLWorkerHelper
import com.itextpdf.tool.xml.html.CssAppliersImpl
import com.itextpdf.tool.xml.html.Tags
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext
import encodeToBase64
import org.apache.commons.io.FilenameUtils
import org.jsoup.select.Elements
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.R
import java.io.*
import java.nio.charset.Charset

fun htmlToPdf(doc: org.jsoup.nodes.Document, file: FileOutputStream) {

    //pdf
    val document = Document()
    val writer = PdfWriter.getInstance(document, file)
    document.open()

    val fontProvider = XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS)
    fontProvider.register("/assets/fonts/DejaVuSans.ttf")
    val cssAppliers = CssAppliersImpl(fontProvider)

    // HTML
    val htmlContext = HtmlPipelineContext(cssAppliers)
    htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory())
    XMLWorkerHelper.getInstance().parseXHtml(writer, document, ByteArrayInputStream(doc.toString().toByteArray(Charsets.UTF_8)), Charset.forName("UTF-8"), fontProvider)
    document.close()
    file.close()
}

fun createDocument(doc: org.jsoup.nodes.Document, filename: String, type: FileExtensions, context: Context): File {

    val img: Elements = doc.getElementsByTag("img")
    lateinit var src: String
    lateinit var base64img: String

    for (el in img) {
        src = el.absUrl("src").replace("file://", "")

        if (type == FileExtensions.HTML) {
            base64img = encodeToBase64(File(src))
            src = "data:image/png;base64, $base64img"
        }

        el.attr("src", src)
    }

    val fileToShare = File(context.cacheDir, filename + ".${type.toString().toLowerCase()}")

    when (type) {

        FileExtensions.HTML -> {
            val writer = BufferedWriter(FileWriter(fileToShare))
            writer.write(doc.toString())
            writer.flush()
            writer.close()
        }

        FileExtensions.PDF -> {
            htmlToPdf(doc, FileOutputStream(fileToShare))
        }
    }
    return fileToShare
}


fun shareDocument(fileToShare: File, context: Context, activity: Activity) {

    val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, fileToShare)
    val shareIntent = ShareCompat.IntentBuilder.from(activity)
            .setType(activity!!.contentResolver.getType(uri))
            .setStream(uri)
            .intent

    //Provide read access
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, FilenameUtils.removeExtension(fileToShare.name))
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    val pm = activity.packageManager

    if (shareIntent.resolveActivity(pm) != null) {
        startActivity(context, Intent.createChooser(shareIntent, R.string.export_lesson.toString()), null)
    }

}


//Share Menu
fun showShareDialog(doc: org.jsoup.nodes.Document, title: String, context: Context, activity: Activity) {

    var type = FileExtensions.PDF
    // custom dialog
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.share_dialog)

    val shareWindow: RadioGroup = dialog.findViewById(R.id.radio_group)

    for (i in 0 until FileExtensions.values().size) {
        val radioButton = RadioButton(context)
        radioButton.text = FileExtensions.values()[i].toString()
        shareWindow.addView(radioButton)
    }

    shareWindow.check(shareWindow.getChildAt(0).id)

    val shareButton: AppCompatButton = dialog.findViewById(R.id.share_document_button)
    shareButton.setOnClickListener { _ ->
        shareDocument(createDocument(doc, title, type, context), context, activity)
        dialog.dismiss()
    }

    val dismissButton: AppCompatButton = dialog.findViewById(R.id.cancel_share_button)
    dismissButton.setOnClickListener { _ ->
        dialog.dismiss()
    }

    dialog.show();

    shareWindow.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
        val childCount = group.childCount
        for (x in 0 until childCount) {
            val btn = group.getChildAt(x) as RadioButton
            if (btn.id == checkedId) {
                type = FileExtensions.valueOf(btn.text.toString())
            }
        }
    })
}