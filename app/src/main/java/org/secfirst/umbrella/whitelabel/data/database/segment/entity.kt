package org.secfirst.umbrella.whitelabel.data.database.segment

import android.os.Parcelable
import com.bluelinelabs.conductor.Controller
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentDetailController
import org.secfirst.umbrella.whitelabel.serialize.PathUtils


@Parcelize
data class Segment(var toolbarTitle: String,
                   var tabTitle: String,
                   var markdowns: List<Markdown> = listOf(),
                   var checklists: List<Checklist>) : Parcelable

@Parcelize
@Table(database = AppDatabase::class, allFields = true, useBooleanGetterSetters = false)
data class Markdown(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "category_id")
        @ForeignKey(stubbedRelationship = true)
        var module: Module? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "subcategory_id")
        @ForeignKey(stubbedRelationship = true)
        var subject: Subject? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        @ForeignKey(stubbedRelationship = true)
        var difficulty: Difficulty? = null,
        var text: String = "",
        var title: String = "",
        var index: String = "",
        var favorite: Boolean = false,
        var basePath: String = "") : BaseModel(), Parcelable {

    constructor(text: String) : this(0,
            null,
            null,
            null, text, recoveryTitle(text), recoveryIndex(text))

    companion object {
        const val TAG_INDEX = "index: "
        const val TAG_TITLE = "title: "
        const val SINGLE_CHOICE = 1
        const val MARKDOWN_IMAGE_TAG = "![image]("
        fun recoveryIndex(text: String) = text.lines()[1].trim().substringAfterLast(TAG_INDEX)
        fun recoveryTitle(text: String): String {
            val res = text.lines()[2].trim().substringAfterLast(TAG_TITLE)
            if (res == "---") {
                return text.lines()[1].trim().substringAfterLast(TAG_TITLE)
            }
            return res
        }
    }
}

fun MutableList<Markdown>.toSegment(toolbarTitle: String,
                                    title: String,
                                    checklists: List<Checklist>): Segment {
    val markdowns = mutableListOf<Markdown>()
    this.forEach { markdown -> markdowns.add(markdown) }
    return Segment(toolbarTitle, title, markdowns.sortedWith(compareBy { it.index }), checklists)
}

fun Segment.toController(host: Controller): SegmentController {
    val checklist = if (this.checklists.isEmpty()) null else this.checklists.last()
    val controller = SegmentController(this.markdowns, this.tabTitle, checklist)
    controller.hostSegmentTabControl = host as HostSegmentTabControl
    return controller
}

fun List<Markdown>.toControllers(): List<SegmentDetailController> {
    val controllers = mutableListOf<SegmentDetailController>()
    this.forEach { markdown ->
        val markdowns = mutableListOf<Markdown>()
        markdowns.add(markdown)
        val controller = SegmentDetailController(markdown)
        controllers.add(controller)
    }
    return controllers
}

fun Markdown.removeHead(): Markdown {
    text = if (Markdown.recoveryIndex(text).isNotBlank()) {
        text.substringAfterLast(text.lines()[3])
    } else {
        text.substringAfter(text.lines()[2])
    }
    return this
}

fun String.replaceMarkdownImage(pwd: String) = this.replace(Markdown.MARKDOWN_IMAGE_TAG,
        "${Markdown.MARKDOWN_IMAGE_TAG}file://${PathUtils.basePath()}/$pwd")

interface HostSegmentTabControl {
    fun onTabHostManager(position: Int)
}
