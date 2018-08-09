package org.secfirst.umbrella.whitelabel.data.database.content

import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.secfirst.umbrella.whitelabel.data.*
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase


class Lesson(val categories: MutableList<Category> = arrayListOf(), val forms: MutableList<Form> = arrayListOf())

@Table(database = AppDatabase::class)
data class Category(
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
        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "category_id")
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
        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "child_id")
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
                    .where(Markdown_Table.category_id.eq(id))
                    .queryList()
        }
        return markdowns
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

inline fun MutableList<Category>.walkChild(action: (Child) -> Unit) {
    this.forEach { category ->
        category.subcategories.forEach { subcategory ->
            subcategory.children.forEach(action)
        }
    }
}

