package org.secfirst.umbrella.whitelabel.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Lesson
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import java.io.Serializable


class Root(val elements: MutableList<Element> = arrayListOf(), val forms: MutableList<Form> = arrayListOf()) {

    fun convertRootToLesson(): Lesson {
        val categories: MutableList<Category> = mutableListOf()
        var subCategories: MutableList<Subcategory> = mutableListOf()
        var children: MutableList<Child> = mutableListOf()

        this.elements.forEach { element ->
            val category = element.convertToCategory
            categories.add(category)
            element.children.forEach { subElement ->

                val subCategory = subElement.convertToSubCategory
                subCategories.add(subCategory)
                subElement.children.forEach { subElementChild ->

                    val child = subElementChild.convertToChild
                    children.add(child)
                }
                subCategory.children = children
                children = mutableListOf()
            }
            category.subcategories = subCategories
            subCategories = mutableListOf()
        }
        return Lesson(categories)
    }
}

data class Element(
        var id: Long = 0,
        var index: Int = 0,
        var title: String = "",
        var description: String = "",
        var markdowns: MutableList<Markdown> = arrayListOf(),
        var children: MutableList<Element> = arrayListOf(),
        var checklist: MutableList<Checklist> = arrayListOf(),
        var rootDir: String = "",
        var path: String = "")

@Table(database = AppDatabase::class, allFields = true)
data class Markdown(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "category_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var category: Category? = null,

        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "subcategory_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var subcategory: Subcategory? = null,

        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "child_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var child: Child? = null,


        var text: String = "") : BaseModel() {
    constructor(text: String) : this(0, null, null, null, text)
}

@Table(database = AppDatabase::class)
data class Checklist(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,

        @Column
        var index: Int = 0,

        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "category_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var category: Category? = null,

        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "subcategory_id")
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                onDelete = ForeignKeyAction.CASCADE,
                stubbedRelationship = true)
        var subcategory: Subcategory? = null,


        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "child_id")
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
        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "checklist_id")
        var checklist: Checklist? = null,
        @Column
        var label: String = "") : BaseModel()


@Table(database = AppDatabase::class, useBooleanGetterSetters = false)
data class Form(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var title: String = "",
        var answers: MutableList<Answer> = mutableListOf(),
        @Column
        var active: Boolean = false,
        @Column(name = "form_model_id")
        var referenceId: Long = 0,
        @Column
        var date: String = "",
        var screens: MutableList<Screen> = arrayListOf()) : BaseModel(), Serializable {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "screens")
    fun oneToManyScreens(): MutableList<Screen> {
        if (screens.isEmpty()) {
            screens = SQLite.select()
                    .from(Screen::class.java)
                    .where(Screen_Table.form_id.eq(id))
                    .queryList()
        }
        return screens
    }

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "answers")
    fun oneToManyAnswer(): MutableList<Answer> {
        if (answers.isEmpty()) {
            answers = SQLite.select()
                    .from(Answer::class.java)
                    .where(Answer_Table.form_id.eq(id))
                    .queryList()
        }
        return answers
    }
}

@Table(database = AppDatabase::class)
data class Screen(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var title: String = "",
        @JsonIgnore
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                deleteForeignKeyModel = false,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "form_id")
        var form: Form? = null,
        var items: MutableList<Item> = arrayListOf()) : BaseModel(), Serializable {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "items")
    fun oneToManyItems(): MutableList<Item> {
        if (items.isEmpty()) {
            items = SQLite.select()
                    .from(Item::class.java)
                    .where(Item_Table.screen_id.eq(id))
                    .queryList()
        }
        return items
    }
}

@Table(database = AppDatabase::class)
data class Item(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var name: String = "",
        @Column
        var type: String = "",
        @Column
        var label: String = "",
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                deleteForeignKeyModel = false,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "screen_id")
        var screen: Screen? = null,
        var options: MutableList<Option> = arrayListOf(),
        @Column
        var value: String = "",
        @Column
        var hint: String = "") : BaseModel(), Serializable {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "options")
    fun oneToManyOptions(): MutableList<Option> {
        if (options.isEmpty()) {
            options = SQLite.select()
                    .from(Option::class.java)
                    .where(Option_Table.item_id.eq(id))
                    .queryList()
        }
        return options
    }
}

@Table(database = AppDatabase::class, allFields = false)
data class Option(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var label: String = "",
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                deleteForeignKeyModel = false,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "item_id")
        var item: Item? = null,
        @Column
        var value: String = "") : BaseModel(), Serializable

@Table(database = AppDatabase::class, useBooleanGetterSetters = false)
data class Answer(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var textInput: String = "",
        @Column
        var choiceInput: Boolean = false,
        @Column
        var itemId: Long = 0,
        @Column
        var optionId: Long = 0,
        @ForeignKey(onUpdate = ForeignKeyAction.CASCADE,
                deleteForeignKeyModel = false,
                stubbedRelationship = true)
        @ForeignKeyReference(foreignKeyColumnName = "id", columnName = "form_id")
        var form: Form? = null) : Serializable

val Element.convertToCategory: Category
    get() {
        val category = Category()
        category.checklist = this.checklist
        category.index = this.index
        category.description = this.description
        category.markdowns = this.markdowns
        category.path = this.path
        category.rootDir = this.rootDir
        category.title = this.title
        return category
    }

val Element.convertToSubCategory: Subcategory
    get() {
        val subcategory = Subcategory()
        subcategory.checklist = this.checklist
        subcategory.index = this.index
        subcategory.description = this.description
        subcategory.markdowns = this.markdowns
        subcategory.path = this.path
        subcategory.rootDir = this.rootDir
        subcategory.title = this.title
        return subcategory
    }

val Element.convertToChild: Child
    get() {
        val child = Child()
        child.checklist = this.checklist
        child.index = this.index
        child.description = this.description
        child.markdowns = this.markdowns
        child.path = this.path
        child.rootDir = this.rootDir
        child.title = this.title
        return child
    }

inline fun MutableList<Element>.walkSubElement(action: (Element) -> Unit) {
    this.forEach { element ->
        element.children.forEach(action)
    }
}

inline fun MutableList<Element>.walkChild(action: (Element) -> Unit) {
    this.forEach { element ->
        element.children.forEach { subElement ->
            subElement.children.forEach(action)
        }
    }
}

