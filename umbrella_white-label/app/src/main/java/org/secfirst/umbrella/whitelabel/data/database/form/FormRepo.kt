package org.secfirst.umbrella.whitelabel.data.database.form

import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.data.Screen

interface FormRepo {

    suspend fun removeForm(form: Form)

    suspend fun persistFormData(answer: Answer)

    suspend fun persistForm(form: Form)

    suspend fun loadModelForms(): List<Form>

    suspend fun loadAnswerBy(formId: Long): List<Answer>

    suspend fun loadActiveForms(): List<Form>

    suspend fun loadScreenBy(formId: Long): List<Screen>

    suspend fun loadFormIdBy(title: String): Long

}