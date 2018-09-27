package org.secfirst.umbrella.whitelabel.data.database.content


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel


class ContentData(val categories: MutableList<Category> = mutableListOf())


@Table(database = AppDatabase::class)
open class Category(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var index: Int = 0,
        @Column
        var title: String = "",
        @Column
        var description: String = "",
        var markdowns: MutableList<Markdown> = arrayListOf(),
        var subcategories: MutableList<Subcategory> = arrayListOf(),
        var checklist: MutableList<Checklist> = arrayListOf(),
        @Column
        var rootDir: String = "",
        @Column
        var path: String = "",
        var icon: String = "",
        @Column
        @JsonIgnore
        var resourcePath: String = "") : BaseModel() {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "markdowns")
    fun oneToManyMarkdowns(): MutableList<Markdown> {
        if (markdowns.isEmpty()) {
            markdowns = SQLite.select()
                    .from(Markdown::class.java)
                    .where(Markdown_Table.category_id.eq(id))
                    .queryList()
        }
        return markdowns
    }

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "subcategories")
    fun oneToManySubcategory(): MutableList<Subcategory> {
        if (subcategories.isEmpty()) {
            subcategories = SQLite.select()
                    .from(Subcategory::class.java)
                    .where(Subcategory_Table.category_id.eq(id))
                    .queryList()
        }
        return subcategories
    }


    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "checklist")
    fun oneToManyChecklist(): MutableList<Checklist> {
        if (checklist.isEmpty()) {
            checklist = SQLite.select()
                    .from(Checklist::class.java)
                    .where(Checklist_Table.category_id.eq(id))
                    .queryList()
        }
        return checklist
    }

}

@Table(database = AppDatabase::class)
data class Subcategory(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "category_id")
        var category: Category? = null,
        @Column
        var index: Int = 0,
        @Column
        var title: String = "",
        @Column
        var description: String = "",
        var markdowns: MutableList<Markdown> = arrayListOf(),
        var children: MutableList<Child> = arrayListOf(),
        var checklist: MutableList<Checklist> = arrayListOf(),
        @Column
        var rootDir: String = "",
        @Column
        var path: String = "") : BaseModel() {


    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "markdowns")
    fun oneToManyMarkdowns(): MutableList<Markdown> {
        if (markdowns.isEmpty()) {
            markdowns = SQLite.select()
                    .from(Markdown::class.java)
                    .where(Markdown_Table.category_id.eq(id))
                    .queryList()
        }
        return markdowns
    }

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "children")
    fun oneToManyChildren(): MutableList<Child> {
        if (children.isEmpty()) {
            children = SQLite.select()
                    .from(Child::class.java)
                    .where(Child_Table.subcategory_id.eq(id))
                    .queryList()
        }
        return children
    }


    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "checklist")
    fun oneToManyChecklist(): MutableList<Checklist> {
        if (checklist.isEmpty()) {
            checklist = SQLite.select()
                    .from(Checklist::class.java)
                    .where(Checklist_Table.category_id.eq(id))
                    .queryList()
        }
        return checklist
    }

}

@Table(database = AppDatabase::class)
data class Child(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        var subcategory: Subcategory? = null,
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
        var path: String = "") : BaseModel() {


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
                    .where(Checklist_Table.child_id.eq(id))
                    .queryList()
        }
        return checklist
    }
}

inline fun MutableList<Category>.walkChild(action: (Child) -> Unit) {
    this.forEach { category ->
        category.subcategories.forEach { subcategory ->
            subcategory.children.forEach(action)
        }
    }
}

inline fun MutableList<Category>.walkSubcategory(action: (Subcategory) -> Unit) {
    this.forEach { category ->
        category.subcategories.forEach(action)
    }
}

@Table(database = AppDatabase::class, allFields = true)
data class Markdown(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "category_id")
        @ForeignKey(stubbedRelationship = true)
        var category: Category? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "subcategory_id")
        @ForeignKey(stubbedRelationship = true)
        var subcategory: Subcategory? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        @ForeignKey(stubbedRelationship = true)
        var child: Child? = null,
        var text: String = "",
        var title: String = "",
        var index: String = "") : BaseModel() {

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
        var category: Category? = null,

        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "subcategory_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var subcategory: Subcategory? = null,


        @ForeignKeyReference(foreignKeyColumnName = "idReference", columnName = "child_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var child: Child? = null,

        @JsonProperty("list")
        var content: MutableList<Content> = arrayListOf()) : BaseModel() {


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
        var label: String = "") : BaseModel()