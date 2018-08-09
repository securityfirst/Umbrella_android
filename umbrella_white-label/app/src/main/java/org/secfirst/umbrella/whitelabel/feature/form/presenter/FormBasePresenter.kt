package org.secfirst.umbrella.whitelabel.feature.form.presenter

import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.form.view.FormView

interface FormBasePresenter<V : FormView, I : FormBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadModelForms()

    fun submitLoadActiveForms()

    fun submitInsert(answer: Answer)

    fun submitForm(form: Form)

    fun submitDeleteForm(form: Form)

}