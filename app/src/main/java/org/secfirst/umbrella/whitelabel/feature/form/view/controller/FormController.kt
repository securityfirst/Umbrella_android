package org.secfirst.umbrella.whitelabel.feature.form.view.controller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.form_progress.*
import kotlinx.android.synthetic.main.form_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.form.DaggerFormComponent
import org.secfirst.umbrella.whitelabel.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.form.presenter.FormBasePresenter
import org.secfirst.umbrella.whitelabel.feature.form.view.FormUI
import org.secfirst.umbrella.whitelabel.feature.form.view.FormView
import org.secfirst.umbrella.whitelabel.feature.form.view.adapter.FormAdapter
import org.secfirst.umbrella.whitelabel.misc.BundleExt.Companion.EXTRA_ACTIVE_FORM
import org.secfirst.umbrella.whitelabel.misc.hideKeyboard
import javax.inject.Inject

class FormController(bundle: Bundle) : BaseController(bundle), FormView, StepperLayout.StepperListener {

    @Inject
    internal lateinit var presenter: FormBasePresenter<FormView, FormBaseInteractor>

    var editTextList = mutableListOf<HashMap<EditText, Answer>>()
    var radioButtonList = mutableListOf<HashMap<RadioButton, Answer>>()
    var checkboxList = mutableListOf<HashMap<CheckBox, Answer>>()

    private var listOfViews: MutableList<FormUI> = mutableListOf()
    private var totalScreens: Int = 0

    constructor(activeForm: ActiveForm) : this(Bundle().apply {
        putSerializable(EXTRA_ACTIVE_FORM, activeForm)
    })

    private val activeForm by lazy { args.getSerializable(EXTRA_ACTIVE_FORM) as ActiveForm }

    override fun onAttach(view: View) {
        super.onAttach(view)
        disableNavigation()
        enableArrowBack(true)
        stepperLayout.adapter = FormAdapter(activeForm.form, this, listOfViews)
        stepperLayout.setListener(this)
        presenter.onAttach(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.form_view, container, false)
        createFormUI()
        totalScreens = activeForm.form.screens.size
        return view
    }

    override fun onDestroy() {
        enableNavigation()
        super.onDestroy()
    }

    private fun createFormUI() {
        activeForm.form.let {
            for (view in it.screens)
                listOfViews.add(FormUI(view, activeForm.answers))
        }
    }

    override fun onCompleted(completeButton: View?) {
        bindCheckboxValue()
        bindEditTextValue()
        bindRadioButtonValue()
        hideKeyboard()
        presenter.submitActiveForm(activeForm)
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
                answer.activeForm = activeForm
                answer.choiceInput = checkbox.isChecked
                if (answer.choiceInput) {
                    activeForm.answers.add(answer)
                }
            }
        }
    }

    private fun bindEditTextValue() {
        editTextList.forEach { map ->
            for (entry in map) {
                val answer = entry.value
                val editText = entry.key
                answer.activeForm = activeForm
                answer.textInput = editText.text.toString()
                if (answer.textInput.isNotEmpty()) {
                    activeForm.answers.add(answer)
                }
            }
        }
    }

    private fun bindRadioButtonValue() {
        radioButtonList.forEach { map ->
            for (entry in map) {
                val answer = entry.value
                val radioButton = entry.key
                answer.activeForm = activeForm
                answer.choiceInput = radioButton.isChecked
                if (answer.choiceInput) {
                    activeForm.answers.add(answer)
                }
            }
        }

    }

    private fun closeView() {
        router.popCurrentController()
    }

    override fun onStepSelected(newStepPosition: Int) {
        setProgress(newStepPosition)
    }

    @SuppressLint("SetTextI18n")
    private fun setProgress(newStepPosition: Int) {
        val size = totalScreens
        var percentage = newStepPosition * 100 / totalScreens
        if (newStepPosition > 0) {
            progressAnswer.progress = percentage
            titleProgressAnswer.text = "$percentage%"
        }

        if (newStepPosition == size - 1) {
            percentage = 100
            progressAnswer.progress = percentage
            titleProgressAnswer.text = "$percentage%"
        }
    }

    override fun showActiveFormWLoad(result: Boolean) {
        if (result) closeView()
    }

    override fun onError(verificationError: VerificationError?) {}

    override fun onReturn() {}


    override fun getEnableBackAction() = true

    override fun getToolbarTitle() = activeForm.title


}