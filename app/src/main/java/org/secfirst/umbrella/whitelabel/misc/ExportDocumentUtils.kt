package org.secfirst.umbrella.whitelabel.misc

import android.content.Context
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
import encodeToBase64
import org.jsoup.select.Elements
import java.io.*

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
    //htmlContext.imageProvider = Base64ImageProvider()

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
