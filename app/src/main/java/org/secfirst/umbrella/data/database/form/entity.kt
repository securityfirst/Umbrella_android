package org.secfirst.umbrella.data.database.form

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fasterxml.jackson.annotation.JsonIgnore
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModel
import org.jsoup.Jsoup
import org.secfirst.advancedsearch.models.SearchResult
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.feature.form.FieldType
import org.secfirst.umbrella.feature.form.hasAnswer
import java.io.Serializable


@Table(database = AppDatabase::class, useBooleanGetterSetters = false)
data class Form(
        @PrimaryKey
        var path: String = "",
        @Column
        var title: String = "",
        @Column
        var deeplinkTitle: String = "",
        var screens: MutableList<Screen> = arrayListOf()) : BaseModel(), Serializable {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "screens")
    fun oneToManyScreens(): MutableList<Screen> {
        if (screens.isEmpty()) {
            screens = SQLite.select()
                    .from(Screen::class.java)
                    .where(Screen_Table.form_path.eq(path))
                    .queryList()
        }
        return screens
    }
}

@Table(database = AppDatabase::class)
data class Screen(
        @PrimaryKey(autoincrement = true)
        var id: Long = 0,
        @Column
        var title: String = "",
        @JsonIgnore
        @ForeignKey(stubbedRelationship = true)
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
        @ForeignKey(stubbedRelationship = true)
        var screen: Screen? = null,
        var options: MutableList<Option> = arrayListOf(),
        @Column
        var value: String = "",
        @Column
        var hint: String = "") : Serializable {

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
        @ForeignKey(stubbedRelationship = true)
        var item: Item? = null,
        @Column
        var value: String = "") : BaseModel(), Serializable

@Table(database = AppDatabase::class)
data class ActiveForm(@PrimaryKey(autoincrement = true)
                      var id: Long = 0,
                      var form: Form = Form(),
                      @Column(name = "form_sha1")
                      var sha1Form: String = "",
                      @Column
                      var date: String = "",
                      @Column
                      var title: String = "",
                      var answers: MutableList<Answer>? = arrayListOf()) : BaseModel(), Serializable {

    @OneToMany(methods = [(OneToMany.Method.ALL)], variableName = "answers")
    fun oneToManyAnswers(): MutableList<Answer>? {
        answers?.let {
            if (it.isEmpty()) {
                answers = SQLite.select()
                        .from(Answer::class.java)
                        .where(Answer_Table.activeForm_id.eq(id))
                        .queryList()
            }
        }
        return answers
    }
}

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
                onDelete = ForeignKeyAction.CASCADE,
                deleteForeignKeyModel = false, stubbedRelationship = true)
        var activeForm: ActiveForm? = null) : Serializable

fun ActiveForm.asHTML(): String {
    var fileName = this.title.replace("[^a-zA-Z0-9.-]", "_")
    if (fileName.length > 50) fileName = fileName.substring(0, 50)
    val doc = Jsoup.parse("")
    val head = doc.head()
    head.append("<meta charset='UTF-8'>")
    val body = doc.body()
    body.attr("style", "display:block;width:100%;")
    doc.title(fileName)
    body.append("<h1>" + this.title + "</h1>")

    for (screen in this.form.screens) {
        body.append("<h3>" + screen.title + "</h3>")
        body.append("<form>")
        for (item in screen.items) {
            val paragraph = body.append("<p></p>")
            paragraph.append("<h5>" + item.name + "</h5>")
            when (item.type) {
                FieldType.TEXT_INPUT.value -> {
                    val answer = item.hasAnswer(this.answers)
                    paragraph.append("<input type='text' value='${answer.textInput}' readonly />")
                }
                FieldType.TEXT_AREA.value -> {
                    val answer = item.hasAnswer(this.answers)
                    paragraph.append("<textarea rows='4' cols='50' readonly>${answer.textInput}</textarea>")
                }
                FieldType.MULTIPLE_CHOICE.value -> {
                    for (formOption in item.options) {
                        val answer = formOption.hasAnswer(this.answers)
                        paragraph.append("<label><input type='checkbox' " + answer.choiceInput + " readonly>" + formOption.label + "</label><br>")
                    }
                }
                FieldType.SINGLE_CHOICE.value -> {
                    for (formOption in item.options) {
                        val answer = formOption.hasAnswer(this.answers)
                        paragraph.append("<label><input type='radio' " + answer.choiceInput + " readonly>" + formOption.label + "</label><br>")
                    }
                }
            }
        }
        body.append("</form>")
    }
    return doc.html()
}

fun MutableList<Form>.associateFormForeignKey() {
    this.forEach { form ->
        form.screens.forEach { screen ->
            screen.form = form
            screen.items.forEach { item ->
                item.screen = screen
                item.options.forEach { option -> option.item = item }
            }
        }
    }
}

fun Form.toSearchResult(): SearchResult {
    return SearchResult(this.title, "Summary of ${this.title}"
    ) { c: Context ->
        val withoutLanguage = this.path.split("/").drop(1).joinToString("/")
        c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("umbrella://forms/$withoutLanguage")))
    }
}