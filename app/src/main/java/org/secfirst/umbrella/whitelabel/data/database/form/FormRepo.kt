package org.secfirst.umbrella.whitelabel.data.database.form

import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.data.Screen

interface FormRepo {

    suspend fun removeActiveForm(activeForm: ActiveForm)

    suspend fun persistFormData(answer: Answer)

    suspend fun persistActiveForm(activeForm: ActiveForm): Boolean

    suspend fun loadModelForms(): List<Form>

    suspend fun loadAnswerBy(formId: Long): List<Answer>

    suspend fun loadActiveForms(): List<ActiveForm>

    suspend fun loadScreenBy(formId: Long): List<Screen>
}