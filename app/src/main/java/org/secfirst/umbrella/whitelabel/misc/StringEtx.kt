package org.secfirst.umbrella.whitelabel.misc

import org.jsoup.Jsoup
import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.feature.form.FieldType
import org.secfirst.umbrella.whitelabel.feature.form.hasAnswer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


val currentTime: String
    get() {
        val dateFormat = SimpleDateFormat("dd/M/yyyy hh:mm", Locale.ENGLISH)
        return dateFormat.format(Date())
    }


fun convertDateToString(date: Date?): String {
    val dateFormat: DateFormat
    var dateConvert = ""
    try {
        if (date != null) {
            dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
            dateConvert = dateFormat.format(date)
        }
    } catch (e: Exception) {
        return dateConvert
    }
    return dateConvert
}

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