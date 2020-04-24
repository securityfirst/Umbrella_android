package org.secfirst.umbrella.data.database.checklist

import android.os.Parcelable
import com.commonsware.cwac.anddown.AndDown
import com.fasterxml.jackson.annotation.JsonProperty
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject

@Parcelize
@Table(database = AppDatabase::class, useBooleanGetterSetters = false, cachingEnabled = true)
data class Checklist(
        @JsonProperty("list")
        var content: MutableList<Content> = arrayListOf(),
        @Column
        var custom: Boolean = false,
        @Column
        var title: String = "",
        @PrimaryKey
        var id: String = "",
        @Column
        var index: Int = 0,
        @Column
        var favorite: Boolean = false,
        @Column
        var progress: Int = 0,
        @Column
        var pathways: Boolean = false,
        @ForeignKey(stubbedRelationship = true)
        var module: Module? = null,

        @ForeignKey(stubbedRelationship = true)
        var subject: Subject? = null,

        @ForeignKey(stubbedRelationship = true)
        var difficulty: Difficulty? = null) : Parcelable {


    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "content")
    fun oneToManyContent(): MutableList<Content> {
        if (content.isEmpty()) {
            content = SQLite.select()
                    .from(Content::class.java)
                    .where(Content_Table.checklist_id.eq(id))
                    .queryList()
        }
        return content
    }
}

@Parcelize
@Table(database = AppDatabase::class, useBooleanGetterSetters = false)
class Content(
        @Column
        var check: String = "",
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
        var checklist: Checklist? = null,
        @Column
        var label: String = "",
        @Column
        var disable: Boolean = false,
        @Column
        var value: Boolean = false) : Parcelable

class Dashboard(var items: List<Item> = listOf()) {

    data class Item(var title: String = "",
                    var id: Long = 0,
                    var progress: Int = 0,
                    var label: String = "",
                    var levelLabel: Int = 0,
                    var checklist: Checklist? = null,
                    var difficulty: Difficulty? = null,
                    var footer : Boolean = false) {

        constructor(progress: Int, label: String,
                    checklist: Checklist?,
                    difficulty: Difficulty?,
                    levelLabel: Int = 0) : this("", 0, progress, label, levelLabel, checklist, difficulty)
    }
}

fun Checklist.covertToHTML(): String {
    var body = "<html><head><meta Content-Type: text/html; charset=\"UTF-8\"></head><body style=\"font-family: Roboto; font-size:16px; font-weight: normal;\" >"
    this.content.forEach { content ->
        if (content.check.isNotEmpty()) {
            body += if (content.value) "✓ ${content.check}" else "✗ ${content.check}"
        }
        body += "<br>"
    }
    return body
}

fun String.convertToMarkdown(): String {
    val andDown = AndDown()
    return andDown.markdownToHtml(this, AndDown.HOEDOWN_EXT_QUOTE, 0)
    }

interface ContentMonitor {
    fun onContentProgress(percentage: Int)
}