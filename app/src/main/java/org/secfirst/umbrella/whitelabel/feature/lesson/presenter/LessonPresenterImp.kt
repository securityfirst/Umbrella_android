package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.toLesson
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown.Companion.SINGLE_CHOICE
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class LessonPresenterImp<V : LessonView, I : LessonBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LessonBasePresenter<V, I> {


    override fun submitSelectHead(moduleId: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val module = it.fetchLesson(moduleId)
                module?.let { safeModule ->
                    if (safeModule.markdowns.size > SINGLE_CHOICE) {
                        getView()?.startSegmentController(safeModule)
                    }
                    if (safeModule.markdowns.size == SINGLE_CHOICE) {
                        val singleMarkdown = safeModule.markdowns.last()
                        getView()?.startSegmentDetail(singleMarkdown)
                    }
                }
            }
        }
    }

    override fun submitSelectLesson(subject: Subject) {
        launchSilent(uiContext) {
            interactor?.let {
                val difficultyPreferred = it.fetchDifficultyPreferredBy(subject.id)
                val subjectMarkdown = it.fetchMarkdownsBy(subject.id)

                if (difficultyPreferred != null)
                    difficultyPreferred.difficulty?.let { safePreferred ->
                        getView()?.startDeferredSegment(safePreferred)
                    }
                else if (subject.difficulties.isEmpty() && subjectMarkdown.isNotEmpty()) {
                    getView()?.startSegmentController(subject)
                } else {
                    getView()?.startDifficultyController(subject)
                }
            }
        }
    }

    override fun submitLoadAllLesson() {
        launchSilent(uiContext) {
            interactor?.let {
                val lessons = it.fetchLessons()
                        .asSequence()
                        .filter { lesson -> lesson.title != "" }
                        .toList()
                getView()?.showAllLesson(lessons.toLesson())
            }
        }
    }
}