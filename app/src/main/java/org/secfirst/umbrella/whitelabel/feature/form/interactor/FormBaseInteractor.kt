package org.secfirst.umbrella.whitelabel.feature.form.interactor

import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.data.Screen
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor


interface FormBaseInteractor : BaseInteractor {

    suspend fun insertFormData(answer: Answer)

    suspend fun insertActiveForm(activeForm: ActiveForm) : Boolean

    suspend fun deleteActiveForm(activeForm: ActiveForm)

    suspend fun fetchModalForms(): List<Form>

    suspend fun fetchActiveForms(): List<ActiveForm>

    suspend fun fetchAnswerBy(formId: Long): List<Answer>

    suspend fun fetchScreenBy(formId: Long): List<Screen>
}