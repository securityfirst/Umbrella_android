package org.secfirst.umbrella.whitelabel.data.database.segment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
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
import org.secfirst.umbrella.whitelabel.serialize.PathUtils


@Parcelize
@Table(database = AppDatabase::class,
        allFields = true, useBooleanGetterSetters = false, cachingEnabled = true)
data class Markdown(
        @PrimaryKey
        var id: String = "",
        var text: String = "",
        var title: String = "",
        var index: String = "",
        var favorite: Boolean = false,
        var basePath: String = "",
        @ForeignKey(stubbedRelationship = true)
        var module: Module? = null,
        @ForeignKey(stubbedRelationship = true)
        var subject: Subject? = null,
        @ForeignKey(stubbedRelationship = true)
        var difficulty: Difficulty? = null) : Parcelable {

    constructor(sha1ID: String, text: String) : this(sha1ID, text, recoveryTitle(text), recoveryIndex(text))

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

fun List<Markdown>.toSegmentController(pChecklist: List<Checklist>): SegmentController {
    val checklist = if (pChecklist.isEmpty()) null else pChecklist.last()
    return SegmentController(ArrayList(this.ids()), checklist?.id ?: "")
}

fun List<Markdown>.toSegmentDetailControllers(): List<SegmentDetailController> {
    val controllers = mutableListOf<SegmentDetailController>()
    this.forEach { markdown ->
        val controller = SegmentDetailController(markdown)
        controllers.add(controller)
    }
    return controllers
}

fun MutableList<Markdown>.sortByIndex() = sortedWith(compareBy { it.index.toInt() })

fun Markdown.removeHead(): Markdown {
    text = text.substringAfterLast(text.lines()[3])
    return this
}

fun Markdown.toSearchResult(): SearchResult {
    return SearchResult(
            title,
            text.substring(0, Math.min(text.length, 300))
    ) { c: Context -> c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("umbrella://lessons/$id")))
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

