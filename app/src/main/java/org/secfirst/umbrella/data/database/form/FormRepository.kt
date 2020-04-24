package org.secfirst.umbrella.data.database.form

import javax.inject.Inject

class FormRepository @Inject constructor(private val formDao: FormDao) : FormRepo {

    override suspend fun loadForm(formTitle: String) = formDao.getForm(formTitle)

    override suspend fun removeActiveForm(activeForm: ActiveForm) = formDao.delete(activeForm)

    override suspend fun loadScreenBy(sh1ID: String): List<Screen> = formDao.getScreenBy(sh1ID)

    override suspend fun persistActiveForm(activeForm: ActiveForm) = formDao.saveActiveForm(activeForm)

    override suspend fun persistFormData(answer: Answer) = formDao.insertAnswer(answer)

    override suspend fun loadModelForms() = formDao.getAllFormModel()

    override suspend fun loadAnswerBy(formId: Long) = formDao.getAnswerBy(formId)

    override suspend fun loadActiveForms(): List<ActiveForm> = formDao.getAllActiveForms()

}