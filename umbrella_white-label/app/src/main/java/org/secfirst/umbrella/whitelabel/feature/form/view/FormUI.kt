package org.secfirst.umbrella.whitelabel.feature.form.view


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import org.jetbrains.anko.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Item
import org.secfirst.umbrella.whitelabel.data.Option
import org.secfirst.umbrella.whitelabel.data.Screen
import org.secfirst.umbrella.whitelabel.feature.form.FieldType
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.FormController


class FormUI(private val screen: Screen, private val answers: List<Answer>?) : AnkoComponent<FormController>, Step {

    override fun createView(ui: AnkoContext<FormController>) = ui.apply {
        val size = 16f

        scrollView {
            background = ColorDrawable(Color.parseColor("#f6f6f6"))
            verticalLayout {
                padding = dip(20)
                screen.items.forEach { item ->
                    when (item.type) {

                        FieldType.LABEL.value ->
                            textView(item.label) {
                                textSize = 18f
                                padding = dip(10)
                                textColor = ContextCompat.getColor(context, R.color.umbrella_purple)
                            }.lparams { gravity = Gravity.CENTER }

                        FieldType.TEXT_AREA.value -> {
                            var answer = Answer()
                            textView(item.label) { textSize = size }.lparams { topMargin = dip(10) }
                            val editText = editText {
                                hint = item.hint
                                getAnswer(item)?.let { answer = it }
                                setText(answer.textInput)

                            }.lparams(width = matchParent)

                            answer.itemId = item.id
                            bindEditText(answer, editText, ui)
                        }
                        FieldType.TEXT_INPUT.value -> {
                            var answer = Answer()
                            textView(item.label) { textSize = size }.lparams { topMargin = dip(10) }
                            val editText = editText {
                                hint = item.hint
                                getAnswer(item)?.let { answer = it }
                                setText(answer.textInput)
                            }.lparams(width = matchParent)

                            answer.itemId = item.id
                            bindEditText(answer, editText, ui)
                        }
                        FieldType.MULTIPLE_CHOICE.value -> {
                            textView(item.label) { textSize = size }.lparams { topMargin = dip(10) }
                            item.options.forEach { formOption ->
                                var answer = Answer()
                                val checkBox = checkBox {
                                    text = formOption.label
                                    getAnswer(formOption)?.let { answer = it }
                                    isChecked = answer.choiceInput
                                    answer.optionId = formOption.id
                                }
                                bindCheckBox(answer, checkBox, ui)
                            }
                        }
                        FieldType.SINGLE_CHOICE.value -> {
                            textView(item.label)
                            item.options.forEach { formOption ->
                                var answer = Answer()
                                val radioButton = radioButton {
                                    text = formOption.label
                                    getAnswer(formOption)?.let { answer = it }
                                    isChecked = answer.choiceInput
                                    answer.optionId = formOption.id
                                }
                                answer.optionId = formOption.id
                                bindRadioButton(answer, radioButton, ui)
                            }
                        }
                    }
                }

            }.lparams(width = matchParent, height = matchParent)
        }

    }.view

    private fun getAnswer(formOption: Option): Answer? {
        answers?.forEach { answer ->
            if (formOption.id == answer.optionId)
                return answer
        }
        return null
    }

    private fun getAnswer(item: Item): Answer? {
        answers?.forEach { answer ->
            if (item.id == answer.itemId)
                return answer
        }
        return null
    }

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