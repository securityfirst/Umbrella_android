package org.secfirst.umbrella.whitelabel.feature.form.interactor

import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Screen
import org.secfirst.umbrella.whitelabel.data.database.form.FormRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class FormInteractorImp @Inject constructor(private val formRepo: FormRepo) : BaseInteractorImp(), FormBaseInteractor {

    override suspend fun deleteActiveForm(activeForm: ActiveForm) = formRepo.removeActiveForm(activeForm)

    override suspend fun fetchAnswerBy(formId: Long): List<Answer> = formRepo.loadAnswerBy(formId)

    override suspend fun fetchScreenBy(formId: Long): List<Screen> = formRepo.loadScreenBy(formId)

    override suspend fun insertActiveForm(activeForm: ActiveForm) = formRepo.persistActiveForm(activeForm)

    override suspend fun fetchActiveForms(): List<ActiveForm> = formRepo.loadActiveForms()

    override suspend fun insertFormData(answer: Answer) = formRepo.persistFormData(answer)

    override suspend fun fetchModalForms() = formRepo.loadModelForms()
}