package org.secfirst.umbrella.whitelabel.feature.form.view

import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView
import java.io.File

interface FormView : BaseView {

    fun showModelAndActiveForms(modelForms: MutableList<Form>, activeForms: MutableList<ActiveForm>) {}

    fun showShareForm(shareFile: File) {}

    fun showActiveFormWLoad(result: Boolean){}
}