package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView


interface LessonBasePresenter<V : LessonView, I : LessonBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadAllLesson()

    fun submitSelectLesson(subject: Subject)

    fun submitSelectHead(moduleId: Long)
}