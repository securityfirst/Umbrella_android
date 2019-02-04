package org.secfirst.umbrella.whitelabel.data.database.checklist

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject

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

//fun List<Checklist>.toChecklistControllers(): List<ChecklistController> {
//    val controllers = mutableListOf<ChecklistController>()
//    this.forEach { checklist ->
//        val checklists = mutableListOf<Checklist>()
//        checklists.add(checklist)
//        val controller = ChecklistController(checklist.id)
//        controllers.add(controller)
//    }
//    return controllers
//}

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
                    var checklist: Checklist? = null,
                    var difficulty: Difficulty? = null) {

        constructor(progress: Int, label: String,
                    checklist: Checklist?,
                    difficulty: Difficulty?) : this("", 0, progress, label, checklist, difficulty)
    }
}

fun Checklist.covertToHTML(): String {
    var body = ""
    this.content.forEach { content ->
        body += "\n" + if (content.value) "\u2713" + " ${content.check}" else "\u2717" + " ${content.check}"
    }
    return body
}

inline fun <reified T> MutableList<Checklist>.associateChecklist(foreignKey: T) {
    this.forEach { checklist ->
        when (foreignKey) {
            is Module -> checklist.module = foreignKey
            is Subject -> checklist.subject = foreignKey
            is Difficulty -> checklist.difficulty = foreignKey
        }
        checklist.content.forEach { content ->
            content.checklist = checklist
        }
    }
}


