package org.secfirst.umbrella.feature.form.view.controller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.RouterTransaction
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.form_progress.*
import kotlinx.android.synthetic.main.form_view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.form.Answer
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.form.DaggerFormComponent
import org.secfirst.umbrella.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.feature.form.presenter.FormBasePresenter
import org.secfirst.umbrella.feature.form.view.FormUI
import org.secfirst.umbrella.feature.form.view.FormView
import org.secfirst.umbrella.feature.form.view.adapter.FormAdapter
import org.secfirst.umbrella.misc.hideKeyboard
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
        enableNavigation(false)
        stepperLayout?.adapter = FormAdapter(activeForm.form, this, listOfViews)
        stepperLayout?.setListener(this)
        stepperLayout?.setNextButtonColor(ContextCompat.getColor(context, R.color.white))
        stepperLayout?.setBackButtonColor(ContextCompat.getColor(context, R.color.white))
        stepperLayout?.setCompleteButtonColor(ContextCompat.getColor(context, R.color.white))
        presenter.onAttach(this)
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = inflater.inflate(R.layout.form_view, container, false)
        createFormUI()
        totalScreens = activeForm.form.screens.size
        return view
    }

    private fun createFormUI() {
        activeForm.form.let {
            for (view in it.screens)
                listOfViews.add(FormUI(view, activeForm.answers))
        }
    }

    private fun bindAllComponents() {
        bindCheckboxValue()
        bindEditTextValue()
        bindRadioButtonValue()
        hideKeyboard()
    }


    override fun onInject() {
        DaggerFormComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }


    private fun closeView() {
        bindAllComponents()
        presenter.submitActiveForm(activeForm)
        router.pushController(RouterTransaction.with(HostFormController()))
        enableNavigation(true)
    }

    override fun onStepSelected(newStepPosition: Int) {
        setProgress(newStepPosition)
    }

    @SuppressLint("SetTextI18n")
    private fun setProgress(newStepPosition: Int) {
        val size = totalScreens
        titleProgressAnswer.text = "0%"
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
    }

    override fun onError(verificationError: VerificationError?) {}

    override fun onReturn() {}

    private fun setUpToolbar() {
        formToolbar?.let { toolbar ->
            mainActivity.setSupportActionBar(toolbar)
            mainActivity.supportActionBar?.title = activeForm.title
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { onAppBarBackAction() }
        }
    }


    override fun onCompleted(completeButton: View?) = closeView()

    private fun onAppBarBackAction() = closeView()

    override fun handleBack(): Boolean {
        closeView()
        return true
    }


    private fun bindCheckboxValue() {
        checkboxList.forEach { map ->
            for (entry in map) {
                val answer = entry.value
                val checkbox = entry.key
                answer.activeForm = activeForm
                answer.choiceInput = checkbox.isChecked
                if (answer.choiceInput) {
                    activeForm.answers?.add(answer)
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
                    activeForm.answers?.add(answer)
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
                    activeForm.answers?.add(answer)
                }
            }
        }
    }


    companion object {
        private const val EXTRA_ACTIVE_FORM = "active_form"
    }
}