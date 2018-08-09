package org.secfirst.umbrella.whitelabel.feature.form

enum class FieldType(val value: String) {
    TEXT_INPUT("text_input"),
    LABEL("label"),
    TEXT_AREA("text_area"),
    MULTIPLE_CHOICE("multiple_choice"),
    SINGLE_CHOICE("single_choice"),
    HINT("hint")
}