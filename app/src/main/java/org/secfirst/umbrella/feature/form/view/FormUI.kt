package org.secfirst.umbrella.feature.form.view


import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import com.google.android.material.color.MaterialColors
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedTintedCheckBox
import org.jetbrains.anko.appcompat.v7.themedTintedRadioButton
import org.jetbrains.anko.design.themedTextInputEditText
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.form.Answer
import org.secfirst.umbrella.data.database.form.Screen
import org.secfirst.umbrella.feature.form.FieldType
import org.secfirst.umbrella.feature.form.hasAnswer
import org.secfirst.umbrella.feature.form.view.controller.FormController


class FormUI(private val screen: Screen, private val answers: List<Answer>?) : AnkoComponent<FormController>, Step {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun createView(ui: AnkoContext<FormController>) = ui.apply {
        val size = 16f

        scrollView {
            verticalLayout {

                padding = dip(10)
                screen.items.forEach { item ->
                    when (item.type) {

                        FieldType.LABEL.value ->
                            themedTextView(item.label,  theme = R.style.textViewTheme) {
                                textSize = 18f
                            }.lparams { gravity = Gravity.CENTER }

                        FieldType.TEXT_AREA.value -> {
                            val answer = item.hasAnswer(answers)
                            themedTextView(item.label, theme = R.style.textViewTheme) {
                                textSize = size
                            }.lparams { topMargin = dip(5) }
                            val editText = themedTextInputEditText(theme = R.style.EditTextStyle) {
                                hint = item.hint
                                setText(answer.textInput)
                            }.lparams(width = matchParent)
                            answer.itemId = item.id
                            bindEditText(answer, editText, ui)
                        }
                        FieldType.TEXT_INPUT.value -> {
                            val answer = item.hasAnswer(answers)
                            themedTextView(item.label, theme = R.style.textViewTheme) {
                                textSize = size
                            }.lparams { topMargin = dip(5) }
                            val editText = themedTextInputEditText(theme = R.style.EditTextStyle) {
                                hint = item.hint
                                setText(answer.textInput)
                            }.lparams(width = matchParent)
                            answer.itemId = item.id
                            answer.run { bindEditText(answer, editText, ui) }
                        }
                        FieldType.MULTIPLE_CHOICE.value -> {
                            themedTextView(item.label, theme = R.style.textViewTheme) {
                                textSize = size
                            }.lparams { topMargin = dip(5) }
                            item.options.forEach { formOption ->
                                val answer = formOption.hasAnswer(answers)
                                val checkBox = themedTintedCheckBox(R.style.checkBoxStyle) {
                                    text = formOption.label
                                    isChecked = answer.choiceInput
                                }.lparams { topMargin = dip(5) }
                                answer.optionId = formOption.id
                                bindCheckBox(answer, checkBox, ui)
                            }
                        }
                        FieldType.SINGLE_CHOICE.value -> {
                            themedTextView(item.label, theme = R.style.textViewTheme) {
                                textSize = size
                            }
                            radioGroup {
                                item.options.forEach { formOption ->
                                    val answer = formOption.hasAnswer(answers)
                                    val radioButton = themedTintedRadioButton(R.style.radioStyle) {
                                        text = formOption.label
                                        isChecked = answer.choiceInput
                                        textSize = size
                                    }
                                    answer.optionId = formOption.id
                                    bindRadioButton(answer, radioButton, ui)
                                }
                            }
                        }
                    }
                }

            }.lparams(width = matchParent, height = matchParent) {
                topMargin = dip(15)
            }
        }

    }.view

    private fun bindRadioButton(answer: Answer, radioButton: RadioButton, ui: AnkoContext<FormController>) {
        val radioButtonMap = hashMapOf<RadioButton, Answer>()
        radioButtonMap[radioButton] = answer
        ui.owner.radioButtonList.add(radioButtonMap)
    }

    private fun bindCheckBox(answer: Answer, checkBox: CheckBox, ui: AnkoContext<FormController>) {
        val checkboxMap = hashMapOf<CheckBox, Answer>()
        checkboxMap[checkBox] = answer
        ui.owner.checkboxList.add(checkboxMap)
    }

    private fun bindEditText(answer: Answer, editText: EditText, ui: AnkoContext<FormController>) {
        val editTextMap = hashMapOf<EditText, Answer>()
        editTextMap[editText] = answer
        ui.owner.editTextList.add(editTextMap)
    }

    override fun onSelected() {}

    override fun verifyStep(): Nothing? = null

    override fun onError(error: VerificationError) {}
}