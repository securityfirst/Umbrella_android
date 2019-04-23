package org.secfirst.umbrella.feature.lesson.presenter

import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.feature.lesson.view.LessonView


interface LessonBasePresenter<V : LessonView, I : LessonBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadAllLesson()

    fun submitSelectLesson(subject: Subject)

    fun submitSelectHead(moduleId: String)
}