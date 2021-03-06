package org.secfirst.umbrella.feature.form.view


import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedTintedCheckBox
import org.jetbrains.anko.appcompat.v7.themedTintedRadioButton
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.form.Answer
import org.secfirst.umbrella.data.database.form.Screen
import org.secfirst.umbrella.feature.form.FieldType
import org.secfirst.umbrella.feature.form.hasAnswer
import org.secfirst.umbrella.feature.form.view.controller.FormController


class FormUI(private val screen: Screen, private val answers: List<Answer>?) : AnkoComponent<FormController>, Step {

    override fun createView(ui: AnkoContext<FormController>) = ui.apply {
        val size = 16f
        val formTextColor = ContextCompat.getColor(ui.ctx, R.color.default_umbrella_black)

        scrollView {
            background = ColorDrawable(ContextCompat.getColor(context, R.color.form_background))
            verticalLayout {

                padding = dip(10)
                screen.items.forEach { item ->
                    when (item.type) {

                        FieldType.LABEL.value ->
                            textView(item.label) {
                                textSize = 18f
                                textColor = formTextColor
                            }.lparams { gravity = Gravity.CENTER }

                        FieldType.TEXT_AREA.value -> {
                            val answer = item.hasAnswer(answers)
                            textView(item.label) {
                                textSize = size
                                textColor = formTextColor
                            }.lparams { topMargin = dip(5) }
                            val editText = themedEditText(theme = R.style.EditTextStyle) {
                                hint = item.hint
                                setText(answer.textInput)
                                textColor = formTextColor

                            }.lparams(width = matchParent)
                            answer.itemId = item.id
                            bindEditText(answer, editText, ui)
                        }
                        FieldType.TEXT_INPUT.value -> {
                            val answer = item.hasAnswer(answers)
                            textView(item.label) {
                                textSize = size
                                textSize = size
                                textColor = formTextColor
                            }.lparams { topMargin = dip(5) }
                            val editText = themedEditText(theme = R.style.EditTextStyle) {
                                hint = item.hint
                                setText(answer.textInput)
                                textColor = formTextColor
                            }.lparams(width = matchParent)
                            answer.itemId = item.id
                            answer.run { bindEditText(answer, editText, ui) }
                        }
                        FieldType.MULTIPLE_CHOICE.value -> {
                            textView(item.label) {
                                textSize = size
                                textColor = formTextColor
                            }.lparams { topMargin = dip(5) }
                            item.options.forEach { formOption ->
                                val answer = formOption.hasAnswer(answers)
                                val checkBox = themedTintedCheckBox(R.style.checkBoxStyle) {
                                    text = formOption.label
                                    textColor = formTextColor
                                    isChecked = answer.choiceInput
                                }.lparams { topMargin = dip(5) }
                                answer.optionId = formOption.id
                                bindCheckBox(answer, checkBox, ui)
                            }
                        }
                        FieldType.SINGLE_CHOICE.value -> {
                            textView(item.label) {
                                textSize = size
                                textColor = formTextColor
                            }
                            radioGroup {
                                item.options.forEach { formOption ->
                                    val answer = formOption.hasAnswer(answers)
                                    val radioButton = themedTintedRadioButton(R.style.radioStyle) {
                                        text = formOption.label
                                        isChecked = answer.choiceInput
                                        textSize = size
                                        textColor = formTextColor
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