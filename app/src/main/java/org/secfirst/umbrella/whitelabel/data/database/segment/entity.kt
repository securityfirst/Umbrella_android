package org.secfirst.umbrella.whitelabel.data.database.segment

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
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.segment.view.controller.SegmentController
import org.secfirst.umbrella.whitelabel.feature.segment.view.controller.SegmentDetailController
import org.secfirst.umbrella.whitelabel.misc.removeSpecialCharacter
import org.secfirst.umbrella.whitelabel.serialize.PathUtils


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

    constructor(id: String, text: String) : this(id, text, recoveryTitle(text),
            recoveryIndex(text),
            false,
            recoveryTitle(text).removeSpecialCharacter())

    companion object {
        const val FAVORITE_INDEX = "1"
        private const val TAG_INDEX = "index: "
        private const val TAG_TITLE = "title: "
        const val SINGLE_CHOICE = 1
        const val MARKDOWN_IMAGE_TAG = "![image]("
        fun recoveryIndex(text: String) = text.lines()[1].trim().substringAfterLast(TAG_INDEX)
        fun recoveryTitle(text: String): String {
            var res = text.lines()[2].trim().substringAfterLast(TAG_TITLE)
            if (res.trim() == "_") res = ""
            return res
        }
    }
}

fun List<Markdown>.ids(): ArrayList<String> {
    val res = arrayListOf<String>()
    this.forEach { res.add(it.id) }
    return res
}

fun List<Markdown>.toSegmentController(pChecklist: List<Checklist>, isFromDashboard: Boolean = false): SegmentController {
    val checklist = if (pChecklist.isEmpty()) null else pChecklist.last()
    return SegmentController(ArrayList(this.ids()), checklist?.id ?: "", isFromDashboard)
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
})

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

fun String.replaceMarkdownImage(absolutePath: String) = this.replace(Markdown.MARKDOWN_IMAGE_TAG,
        "${Markdown.MARKDOWN_IMAGE_TAG}file://${PathUtils.basePath()}/${PathUtils.getWorkDirectoryFromImage(absolutePath)}")

inline fun <reified T> MutableList<Markdown>.associateMarkdown(foreignKey: T) {
    this.forEach { mark ->
        when (foreignKey) {
            is Module -> mark.module = foreignKey
            is Subject -> mark.subject = foreignKey
            is Difficulty -> mark.difficulty = foreignKey
        }
    }
}

interface HostSegmentTabControl {
    fun moveTabAt(position: Int)
}

