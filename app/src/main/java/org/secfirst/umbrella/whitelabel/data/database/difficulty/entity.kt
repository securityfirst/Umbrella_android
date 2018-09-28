package org.secfirst.umbrella.whitelabel.data.database.difficulty

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.content.*


@Parcelize
@Table(database = AppDatabase::class)
data class Difficulty(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        var subject: Subject? = null,
        @Column
        var index: Int = 0,
        @Column
        var title: String = "",
        @Column
        var description: String = "",
        var markdowns: MutableList<Markdown> = arrayListOf(),
        var checklist: MutableList<Checklist> = arrayListOf(),
        @Column
        var rootDir: String = "",
        @Column
        var path: String = "",
        @JsonIgnore
        var layoutColor: String = "") : BaseModel(), Parcelable {


    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "markdowns")
    fun oneToManyMarkdowns(): MutableList<Markdown> {
        if (markdowns.isEmpty()) {
            markdowns = SQLite.select()
                    .from(Markdown::class.java)
                    .where(Markdown_Table.child_id.eq(id))
                    .queryList()
        }
        return markdowns
    }

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "checklist")
    fun oneToManyChecklist(): MutableList<Checklist> {
        if (checklist.isEmpty()) {
            checklist = SQLite.select()
                    .from(Checklist::class.java)
                    .where(Checklist_Table.difficulty_id.eq(id))
                    .queryList()
        }
        return checklist
    }

    companion object {
        const val BEGINNER = 1
        const val ADVANCED = 2
        const val EXPERT = 3
        const val COLOR_BEGINNER = "#87BD34"
        const val COLOR_ADVANCED = "#F3BC2B"
        const val COLOR_EXPERT = "#B83657"
    }
}

fun MutableList<Difficulty>.orderBySelected(difficultSelected: Difficulty?): MutableList<Difficulty> {
    val difficultOrder = mutableListOf<Difficulty>()
    difficultSelected?.let { difficultOrder.add(it) }
    this.forEach { difficulty ->
        if (difficulty.id != difficultSelected?.id)
            difficultOrder.add(difficulty)

    }
    return difficultOrder
}

fun MutableList<Difficulty>.withColors(): List<Difficulty> {
    val sortedList = this.sortedWith(compareBy { it.index })
    sortedList.forEach { difficulty ->
        when (difficulty.index) {
            Difficulty.BEGINNER -> difficulty.layoutColor = Difficulty.COLOR_BEGINNER
            Difficulty.ADVANCED -> difficulty.layoutColor = Difficulty.COLOR_ADVANCED
            Difficulty.EXPERT -> difficulty.layoutColor = Difficulty.COLOR_EXPERT
            else -> {
                difficulty.layoutColor = Difficulty.COLOR_EXPERT
            }
        }

    }
    return sortedList
}
