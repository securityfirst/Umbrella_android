package org.secfirst.umbrella.feature.form.presenter

import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.form.Answer
import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.feature.form.view.FormView

interface FormBasePresenter<V : FormView, I : FormBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadAllForms()

    fun submitInsert(answer: Answer)

    fun submitActiveForm(activeForm: ActiveForm)

    fun submitDeleteActiveForm(activeForm: ActiveForm)

    fun submitShareFormHtml(activeForm: ActiveForm)

    fun submitFormByURI(uriString: String)

}