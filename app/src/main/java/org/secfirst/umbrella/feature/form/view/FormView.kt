package org.secfirst.umbrella.feature.form.view

import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.feature.base.view.BaseView
import java.io.File

interface FormView : BaseView {

    fun showModelAndActiveForms(modelForms: MutableList<Form>, activeForms: MutableList<ActiveForm>) {}

    fun showShareForm(shareFile: File) {}

    fun showActiveFormWLoad(result: Boolean){}

    fun openSpecificForm(form: Form) {}
}