package org.secfirst.umbrella.whitelabel.data.database.content


import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table


class ContentData(val modules: MutableList<Module> = mutableListOf())


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

inline fun MutableList<Module>.walkChild(action: (Difficulty) -> Unit) {
    this.forEach { category ->
        category.subjects.forEach { subcategory ->
            subcategory.difficulties.forEach(action)
        }
    }
}

inline fun MutableList<Module>.walkSubcategory(action: (Subject) -> Unit) {
    this.forEach { category ->
        category.subjects.forEach(action)
    }
}

@Parcelize
@Table(database = AppDatabase::class, allFields = true)
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
        var index: String = "") : BaseModel(), Parcelable {

    constructor(text: String) : this(0,
            null,
            null,
            null, text, recoveryTitle(text), recoveryIndex(text))

    companion object {
        private const val TAG_INDEX = "index: "
        private const val TAG_TITLE = "title: "

        fun recoveryIndex(text: String) = text.lines()[1].trim().substringAfterLast(TAG_INDEX)
        fun recoveryTitle(text: String) = text.lines()[2].trim().substringAfterLast(TAG_TITLE)
    }

}

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