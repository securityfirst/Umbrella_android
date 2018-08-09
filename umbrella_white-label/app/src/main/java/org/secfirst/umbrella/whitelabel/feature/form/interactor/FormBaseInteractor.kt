package org.secfirst.umbrella.whitelabel.feature.form.interactor

import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.data.Screen
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor


interface FormBaseInteractor : BaseInteractor {

    suspend fun insertFormData(answer: Answer)

    suspend fun insertForm(form: Form)

    suspend fun deleteForm(form: Form)

    suspend fun fetchForms(): List<Form>

    suspend fun fetchActiveForms(): List<Form>

    suspend fun fetchAnswerBy(formId: Long): List<Answer>

    suspend fun fetchScreenBy(formId: Long): List<Screen>

    suspend fun fetchFormIdBy(title: String): Long

}