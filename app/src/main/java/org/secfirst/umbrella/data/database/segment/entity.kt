package org.secfirst.umbrella.data.database.segment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import com.commonsware.cwac.anddown.AndDown
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.android.parcel.Parcelize
import org.secfirst.advancedsearch.models.SearchResult
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.data.disk.defaultContent
import org.secfirst.umbrella.data.disk.getPathRepository
import org.secfirst.umbrella.feature.segment.view.controller.SegmentController
import org.secfirst.umbrella.feature.segment.view.controller.SegmentDetailController
import org.secfirst.umbrella.misc.removeSpecialCharacter


@Parcelize
@Table(database = AppDatabase::class, useBooleanGetterSetters = false, cachingEnabled = true)
data class Markdown(
        @PrimaryKey
        var id: String = "",
        @Column
        var text: String = "",
        @Column
        var title: String = "",
        @Column
        var index: String = "",
        @Column
        var favorite: Boolean = false,
        @Column
        var identifier: String = "",
        @ForeignKey(stubbedRelationship = true)
        var module: Module? = null,
        @ForeignKey(stubbedRelationship = true)
        var subject: Subject? = null,
        @ForeignKey(stubbedRelationship = true)
        var difficulty: Difficulty? = null,
        var isRemove: Boolean = false) : Parcelable {

    constructor(id: String, text: String) : this(id, text, recoveryTitleOrIndex(text, TAG_TITLE),
            recoveryTitleOrIndex(text, TAG_INDEX),
            false,
            recoveryTitleOrIndex(text, TAG_TITLE).removeSpecialCharacter())

    companion object {
        const val FAVORITE_INDEX = "1"
        const val TAG_INDEX = "index: "
        const val TAG_TITLE = "title: "
        const val SINGLE_CHOICE = 1
        const val MARKDOWN_IMAGE_TAG = "![image]("
    }
}

private fun recoveryTitleOrIndex(text: String, tag: String): String {
    val content = text.substringAfter("---").substringBefore("---").lines()
    content.forEach { line ->
        if (line.contains(tag, true)) {
            return line.trim().substringAfter(tag)
        }
    }
    return ""
}

fun List<Markdown>.ids(): ArrayList<String> {
    val res = arrayListOf<String>()
    this.forEach { res.add(it.id) }
    return res
}

fun List<Markdown>.toSegmentController(pChecklist: List<Checklist>, isFromDashboard: Boolean = false): SegmentController {
    val checklist = if (pChecklist.isEmpty()) null else pChecklist.last()
    return SegmentController(ArrayList(this.ids()), checklist?.id
            ?: "", isFromDashboard)
}

fun List<Markdown>.toSegmentDetailControllers(): List<SegmentDetailController> {
    val controllers = mutableListOf<SegmentDetailController>()
    this.forEach { markdown ->
        val controller = SegmentDetailController(markdown)
        controllers.add(controller)
    }
    return controllers
}

fun MutableList<Markdown>.sortByIndex() = sortedWith(compareBy {
    try {
        it.index.toInt()
    } catch (e: Exception) {
        0
    }
}).toMutableList()

fun Markdown.removeHead(): Markdown {
    text = text.substringAfterLast(text.lines()[3])
    return this
}

fun Markdown.toSearchResult(): SearchResult {
    val andDown = AndDown()
    val result = andDown.markdownToHtml(text, AndDown.HOEDOWN_EXT_QUOTE, 0)
    return SearchResult(
            title,
            result
    )
    { c: Context ->
        val withoutLanguage = id.split("/").drop(1).joinToString("/")
        c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("umbrella://$withoutLanguage")))
    }
}

fun String.replaceMarkdownImage(pwd: String): String {
    val absolutePath = pwd.substringAfterLast(getPathRepository())
    val pathSplit = absolutePath.split("/").toMutableList()
    pathSplit[0] = defaultContent()
    val defaultImagePath = "${getPathRepository()}${pathSplit.joinToString("/")}"
    return this.replace(Markdown.MARKDOWN_IMAGE_TAG,
            "${Markdown.MARKDOWN_IMAGE_TAG}file://$defaultImagePath")
}

interface HostSegmentTabControl {
    fun moveTabAt(position: Int)
}

