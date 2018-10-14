package org.secfirst.umbrella.whitelabel.data.database.lesson

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist_Table
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown_Table

data class Lesson(var moduleId: Long,
                  var moduleTitle: String = "",
                  var pathIcon: String = "",
                  var topics: List<Subject> = listOf()) : ExpandableGroup<Subject>(moduleTitle, topics)

@Table(database = AppDatabase::class)
@Parcelize
open class Module(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var index: Int = 0,
        @Column
        var title: String = "",
        @Column
        var description: String = "",
        var markdowns: MutableList<Markdown> = arrayListOf(),
        var subjects: MutableList<Subject> = arrayListOf(),
        var checklist: MutableList<Checklist> = arrayListOf(),
        @Column
        var rootDir: String = "",
        @Column
        var path: String = "",
        var icon: String = "",
        @Column
        @JsonIgnore
        var resourcePath: String = "") : BaseModel(), Parcelable {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "markdowns")
    fun oneToManyMarkdowns(): MutableList<Markdown> {
        if (markdowns.isEmpty()) {
            markdowns = SQLite.select()
                    .from(Markdown::class.java)
                    .where(Markdown_Table.module_id.eq(id))
                    .queryList()
        }
        return markdowns
    }

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "subjects")
    fun oneToManySubcategory(): MutableList<Subject> {
        if (subjects.isEmpty()) {
            subjects = SQLite.select()
                    .from(Subject::class.java)
                    .where(Subject_Table.module_id.eq(id))
                    .queryList()
        }
        return subjects
    }

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "checklist")
    fun oneToManyChecklist(): MutableList<Checklist> {
        if (checklist.isEmpty()) {
            checklist = SQLite.select()
                    .from(Checklist::class.java)
                    .where(Checklist_Table.module_id.eq(id))
                    .queryList()
        }
        return checklist
    }

}

@Parcelize
@Table(database = AppDatabase::class)
data class Subject(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "category_id")
        var module: Module? = null,
        @Column
        var index: Int = 0,
        @Column
        var title: String = "",
        @Column
        var description: String = "",
        var markdowns: MutableList<Markdown> = arrayListOf(),
        var difficulties: MutableList<Difficulty> = arrayListOf(),
        var checklist: MutableList<Checklist> = arrayListOf(),
        @Column
        var rootDir: String = "",
        @Column
        var path: String = "") : BaseModel(), Parcelable {

    constructor() : this(0, null, 0, "", "", arrayListOf(), arrayListOf(), arrayListOf(), "", "")

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "markdowns")
    fun oneToManyMarkdowns(): MutableList<Markdown> {
        if (markdowns.isEmpty()) {
            markdowns = SQLite.select()
                    .from(Markdown::class.java)
                    .where(Markdown_Table.module_id.eq(id))
                    .queryList()
        }
        return markdowns
    }

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "difficulties")
    fun oneToManyChildren(): MutableList<Difficulty> {
        if (difficulties.isEmpty()) {
            difficulties = SQLite.select()
                    .from(Difficulty::class.java)
                    .where(Difficulty_Table.subject_id.eq(id))
                    .queryList()
        }
        return difficulties
    }


    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "checklist")
    fun oneToManyChecklist(): MutableList<Checklist> {
        if (checklist.isEmpty()) {
            checklist = SQLite.select()
                    .from(Checklist::class.java)
                    .where(Checklist_Table.module_id.eq(id))
                    .queryList()
        }
        return checklist
    }
}

inline fun MutableList<Module>.walkThroughSubject(action: (Subject) -> Unit) {
    this.forEach { module ->
        module.subjects.forEach(action)
    }
}

fun List<Module>.toLesson(): List<Lesson> {
    val lessons = mutableListOf<Lesson>()
    val moduleSorted = this.sortedWith(compareBy { it.index })
    moduleSorted.forEach { module ->
        val subjectSorted = module.subjects.sortedWith(compareBy { it.index })
        module.subjects = subjectSorted.toMutableList()
        val lesson = Lesson(module.id, module.title, module.resourcePath, module.subjects)
        lessons.add(lesson)
    }
    return lessons
}