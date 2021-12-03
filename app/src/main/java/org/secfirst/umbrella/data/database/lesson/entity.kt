package org.secfirst.umbrella.data.database.lesson

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Checklist_Table
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.Markdown_Table
import org.secfirst.umbrella.misc.appContext


data class Lesson(
    var moduleId: String,
    var moduleTitle: String = "",
    var pathIcon: String = "",
    var markdowns: MutableList<Markdown> = mutableListOf(),
    var topics: List<Subject> = listOf()
)


@ModelView(database = AppDatabase::class, name = "Module")
open class ModuleModelView(
    @Column
    var id: String = "",
    @Column
    var index: Int = 0,
    @Column
    var title: String = "",
    @Column
    var template: String = "",
    @Column
    var description: String = "",
    var markdowns: MutableList<Markdown> = mutableListOf(),
    var subjects: MutableList<Subject> = mutableListOf(),
    var checklist: MutableList<Checklist> = mutableListOf(),
    @Column
    var rootDir: String = "",
    @Column
    var icon: String = "",
    @Column
    var resourcePath: String = ""
) : BaseModelView() {

    fun toModule(): Module {
        return Module(
            id = this.id,
            index = this.index,
            title = this.title,
            template = this.template,
            description = this.description,
            markdowns = this.markdowns,
            subjects = this.subjects,
            checklist = this.checklist,
            rootDir = this.rootDir,
            icon = this.icon,
            resourcePath = this.resourcePath
        )
    }

    companion object {
        @ModelViewQuery
        @JvmField
        val query: Query = SQLite.select().from(Module::class.java).queryBuilderBase
    }
}

@ModelView(database = AppDatabase::class, name = "Subject")
data class SubjectModelView(
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
    var id: String = "",
    @Column
    var module_id: String = "",
    @Column
    var icon: String = ""
) : BaseModelView() {

    fun toSubject(): Subject {
        return Subject(
            id = this.id,
            index = this.index,
            title = this.title,
            description = this.description,
            markdowns = this.markdowns,
            difficulties = this.difficulties,
            checklist = this.checklist,
            rootDir = this.rootDir,
            icon = this.icon,
            module = Module(id = this.module_id)
        )
    }

    companion object {
        @ModelViewQuery
        @JvmField
        val query: Query =
            SQLite.select().from(SubjectModelView_ViewTable::class.java).queryBuilderBase
    }

}

@Table(database = AppDatabase::class)
@Parcelize
open class Module(
    @PrimaryKey
    var id: String = "",
    @Column
    var index: Int = 0,
    @Column
    var title: String = "",
    @Column
    var template: String = "",
    @Column
    var description: String = "",
    var markdowns: MutableList<Markdown> = arrayListOf(),
    var subjects: MutableList<Subject> = arrayListOf(),
    var checklist: MutableList<Checklist> = arrayListOf(),
    @Column
    var rootDir: String = "",
    var icon: String = "",
    @Column
    @JsonIgnore
    var resourcePath: String = ""
) : Parcelable {

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

    companion object {
        const val FAVORITE_ID = "1"
    }
}

@Table(database = AppDatabase::class)
@Parcelize
data class Subject(
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
    @PrimaryKey
    var id: String = "",
    var icon: String = "",
    @ForeignKey(stubbedRelationship = true)
    var module: Module? = null
) : Parcelable {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "markdowns")
    fun oneToManyMarkdowns(): MutableList<Markdown> {
        if (markdowns.isEmpty()) {
            markdowns = SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.subject_id.eq(id))
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

fun List<Module>.toLesson(): List<Lesson> = map { module ->
    Lesson(module.id, module.title, module.resourcePath, module.markdowns, module.subjects)
}

fun createDefaultFavoriteModule(): Module {
    val favoriteModule = Module()
    favoriteModule.id = Module.FAVORITE_ID
    favoriteModule.index = 1
    favoriteModule.title = appContext().getString(org.secfirst.umbrella.R.string.bookmark_title)
    return favoriteModule
}