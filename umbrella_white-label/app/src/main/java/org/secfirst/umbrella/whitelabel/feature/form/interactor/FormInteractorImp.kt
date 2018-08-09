package org.secfirst.umbrella.whitelabel.feature.form.interactor

import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.data.Screen
import org.secfirst.umbrella.whitelabel.data.database.form.FormRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class FormInteractorImp @Inject constructor(private val formRepo: FormRepo) : BaseInteractorImp(), FormBaseInteractor {

    override suspend fun deleteForm(form: Form) = formRepo.removeForm(form)

    override suspend fun fetchFormIdBy(title: String): Long = formRepo.loadFormIdBy(title)

    override suspend fun fetchAnswerBy(formId: Long): List<Answer> = formRepo.loadAnswerBy(formId)

    override suspend fun fetchScreenBy(formId: Long): List<Screen> = formRepo.loadScreenBy(formId)

    override suspend fun insertForm(form: Form) = formRepo.persistForm(form)

    override suspend fun fetchActiveForms(): List<Form> = formRepo.loadActiveForms()

    override suspend fun insertFormData(answer: Answer) = formRepo.persistFormData(answer)

    override suspend fun fetchForms() = formRepo.loadModelForms()
}