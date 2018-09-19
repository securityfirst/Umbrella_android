package org.secfirst.umbrella.whitelabel.feature.form

import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Item
import org.secfirst.umbrella.whitelabel.data.Option

enum class FieldType(val value: String) {
    TEXT_INPUT("text_input"),
    LABEL("label"),
    TEXT_AREA("text_area"),
    MULTIPLE_CHOICE("multiple_choice"),
    SINGLE_CHOICE("single_choice"),
    HINT("hint")
}

fun Option.hasAnswer(answers: List<Answer>?): Answer {
    answers?.forEach { answer ->
        if (this.id == answer.optionId)
            return answer
    }
    return Answer()
}

fun Item.hasAnswer(answers: List<Answer>?): Answer {
    answers?.forEach { answer ->
        if (this.id == answer.itemId)
            return answer
    }
    return Answer()
}
