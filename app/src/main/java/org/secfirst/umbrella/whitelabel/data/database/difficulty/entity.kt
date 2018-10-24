package org.secfirst.umbrella.whitelabel.data.database.difficulty

import android.os.Parcelable
import com.bluelinelabs.conductor.Controller
import com.fasterxml.jackson.annotation.JsonIgnore
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown_Table
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController


@Parcelize
@Table(database = AppDatabase::class, cachingEnabled = true)
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
                    .where(Markdown_Table.difficulty_id.eq(id))
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


fun MutableList<Difficulty>.orderDifficultyBy(selectDifficultyId: Long): MutableList<Difficulty> {
    val baseIndex = 0
    var selectDifficulty: Difficulty
    for ((index, value) in this.withIndex()) {
        if (value.id == selectDifficultyId) {
            selectDifficulty = value
            this[index] = this[baseIndex]
            this[baseIndex] = selectDifficulty
        }
    }
    return this
}

fun MutableList<Checklist>.toMergeDifficulty() {
    val checklistSorteByDifficulty = mutableListOf<Checklist>()

    for (i in this.indices) {


    }
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

inline fun MutableList<Module>.walkThroughDifficulty(action: (Difficulty) -> Unit) {
    this.forEach { module ->
        module.subjects.forEach { subject ->
            subject.difficulties.forEach(action)
        }
    }
}


@Table(database = AppDatabase::class)
data class TopicPreferred(@PrimaryKey
                          var subjectId: Long = 0,
                          @ForeignKey
                          var difficulty: Difficulty? = null) : BaseModel()

