package org.secfirst.umbrella.whitelabel.feature.form.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError

import kotlinx.android.synthetic.main.form_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.R.id.stepperLayout
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.feature.MainActivity
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.form.DaggerFormComponent
import org.secfirst.umbrella.whitelabel.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.form.presenter.FormBasePresenter
import org.secfirst.umbrella.whitelabel.feature.form.view.FormUI
import org.secfirst.umbrella.whitelabel.feature.form.view.FormView
import org.secfirst.umbrella.whitelabel.feature.form.view.adapter.FormAdapter
import org.secfirst.umbrella.whitelabel.feature.main.OnNavigationBottomView
import org.secfirst.umbrella.whitelabel.misc.BundleExt.Companion.EXTRA_FORM_SELECTED
import org.secfirst.umbrella.whitelabel.misc.currentTime
import org.secfirst.umbrella.whitelabel.misc.hideKeyboard
import javax.inject.Inject

class FormController(bundle: Bundle) : BaseController(bundle), FormView, StepperLayout.StepperListener {

    @Inject
    internal lateinit var presenter: FormBasePresenter<FormView, FormBaseInteractor>
    var editTextList = mutableListOf<HashMap<EditText, Answer>>()
    var radioButtonList = mutableListOf<HashMap<RadioButton, Answer>>()
    var checkboxList = mutableListOf<HashMap<CheckBox, Answer>>()
    private lateinit var onNavigation: OnNavigationBottomView
    private var listOfViews: MutableList<FormUI> = mutableListOf()
    private lateinit var newForm: Form

    constructor(formSelected: Form) : this(Bundle().apply {
        putSerializable(EXTRA_FORM_SELECTED, formSelected)
    })

    private val formSelected by lazy { args.getSerializable(EXTRA_FORM_SELECTED) as Form }

    override fun onAttach(view: View) {
        super.onAttach(view)
        stepperLayout.adapter = FormAdapter(formSelected, this, listOfViews)
        stepperLayout.setListener(this)
        onNavigation = activity as MainActivity
        presenter.onAttach(this)
        onNavigation.hideBottomMenu()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.form_view, container, false)
        createActiveForm()
        return view
    }

    override fun onDestroy() {
        onNavigation.showBottomMenu()
        super.onDestroy()
    }

    private fun createActiveForm() {
        newForm = formSelected
        if (!newForm.active) {
            newForm.referenceId = formSelected.id
            newForm.id = if (formSelected.active) newForm.id else System.currentTimeMillis()
            newForm.active = true
        }
        createFormUI()
    }

    private fun createFormUI() {
        for (view in formSelected.screens)
            listOfViews.add(FormUI(view, formSelected.answers))
    }

    override fun onCompleted(completeButton: View?) {
        newForm.date = currentTime
        bindCheckboxValue()
        bindEditTextValue()
        bindRadioButtonValue()
        hideKeyboard()
        router.popCurrentController()
    }

    override fun onInject() {
        DaggerFormComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }


    private fun bindCheckboxValue() {
        checkboxList.forEach { map ->
            for (entry in map) {
                val answer = entry.value
                val checkbox = entry.key
                answer.choiceInput = checkbox.isChecked
                answer.form = newForm
                if (answer.choiceInput)
                    newForm.answers.add(answer)

            }

            presenter.submitForm(newForm)
        }
    }

    private fun bindEditTextValue() {
        editTextList.forEach { map ->
            for (entry in map) {
                val answer = entry.value
                val editText = entry.key
                answer.textInput = editText.text.toString()
                answer.form = newForm
                if (answer.textInput.isNotEmpty())
                    newForm.answers.add(answer)

            }
            presenter.submitForm(newForm)
        }
    }

    private fun bindRadioButtonValue() {
        radioButtonList.forEach { map ->
            for (entry in map) {
                val answer = entry.value
                val radioButton = entry.key
                answer.choiceInput = radioButton.isChecked
                answer.form = newForm
                if (answer.choiceInput)
                    newForm.answers.add(answer)

            }
            presenter.submitForm(newForm)
        }
    }

    override fun onStepSelected(newStepPosition: Int) {}

    override fun onError(verificationError: VerificationError?) {}

    override fun onReturn() {}

    override fun getTitleToolbar() = formSelected.title

    override fun getEnableBackAction() = true

}