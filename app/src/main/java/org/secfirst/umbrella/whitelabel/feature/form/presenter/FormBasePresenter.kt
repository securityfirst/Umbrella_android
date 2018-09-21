package org.secfirst.umbrella.whitelabel.feature.form.presenter

import org.secfirst.umbrella.whitelabel.data.disk.ActiveForm
import org.secfirst.umbrella.whitelabel.data.disk.Answer
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.form.view.FormView

interface FormBasePresenter<V : FormView, I : FormBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadAllForms()

    fun submitInsert(answer: Answer)

    fun submitActiveForm(activeForm: ActiveForm)

    fun submitDeleteActiveForm(activeForm: ActiveForm)

    fun submitShareFormHtml(activeForm: ActiveForm)

}