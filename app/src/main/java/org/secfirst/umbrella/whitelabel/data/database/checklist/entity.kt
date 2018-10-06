package org.secfirst.umbrella.whitelabel.data.database.checklist

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistController

@Parcelize
@Table(database = AppDatabase::class)
data class Checklist(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @Column
        var index: Int = 0,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "category_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var module: Module? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "subcategory_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var subject: Subject? = null,


        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var difficulty: Difficulty? = null,

        @JsonProperty("list")
        var content: MutableList<Content> = arrayListOf()) : BaseModel(), Parcelable {


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

fun List<Checklist>.toControllers(): List<ChecklistController> {
    val controllers = mutableListOf<ChecklistController>()
    this.forEach { checklist ->
        val checklists = mutableListOf<Checklist>()
        checklists.add(checklist)
        val controller = ChecklistController(checklist)
        controllers.add(controller)
    }
    return controllers
}


@Parcelize
@Table(database = AppDatabase::class)
class Content(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var check: String = "",
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "checklist_id")
        var checklist: Checklist? = null,
        @Column
        var label: String = "") : BaseModel(), Parcelable