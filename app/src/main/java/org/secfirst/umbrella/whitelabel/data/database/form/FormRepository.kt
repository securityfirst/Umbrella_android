package org.secfirst.umbrella.whitelabel.data.database.form

import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Answer
import org.secfirst.umbrella.whitelabel.data.Screen
import javax.inject.Inject

class FormRepository @Inject constructor(private val formDao: FormDao) : FormRepo {

    override suspend fun removeActiveForm(activeForm: ActiveForm) = formDao.delete(activeForm)

    override suspend fun loadScreenBy(formId: Long): List<Screen> = formDao.getScreenBy(formId)

    override suspend fun persistActiveForm(activeForm: ActiveForm) = formDao.saveActiveForm(activeForm)

    override suspend fun persistFormData(answer: Answer) = formDao.insertAnswer(answer)

    override suspend fun loadModelForms() = formDao.getAllFormModel()

    override suspend fun loadAnswerBy(formId: Long) = formDao.getAnswerBy(formId)

    override suspend fun loadActiveForms(): List<ActiveForm> = formDao.getAllActiveForms()

}