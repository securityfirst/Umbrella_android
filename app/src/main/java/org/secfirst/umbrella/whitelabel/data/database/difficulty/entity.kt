package org.secfirst.umbrella.whitelabel.data.database.difficulty

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown_Table


@Parcelize
@Table(database = AppDatabase::class)
data class Difficulty(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE, stubbedRelationship = true)
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
        var layoutColor: String = "") : Parcelable {


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


fun MutableList<Difficulty>.orderDifficulty(selectDifficulty: Difficulty): MutableList<Difficulty> {
    val auxDifficulties = mutableListOf<Difficulty>()
    auxDifficulties.add(selectDifficulty)
    this.forEach {
        if (selectDifficulty.id != it.id)
            auxDifficulties.add(it)
    }
    return auxDifficulties
}

fun defaultDifficulty(markdowns: List<Markdown>, subjectTitle: String): Difficulty {
    val difficulty = Difficulty()
    val subject = Subject()
    subject.title = subjectTitle
    difficulty.markdowns.addAll(markdowns)
    difficulty.subject = subject
    return difficulty
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
data class DifficultyPreferred(@PrimaryKey
                               var subjectId: Long = 0,
                               @ForeignKey
                               var difficulty: Difficulty? = null) : BaseModel()

