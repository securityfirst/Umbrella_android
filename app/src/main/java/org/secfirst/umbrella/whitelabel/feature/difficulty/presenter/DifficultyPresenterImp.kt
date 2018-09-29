package org.secfirst.umbrella.whitelabel.feature.difficulty.presenter

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.withColors
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegment
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class DifficultyPresenterImp<V : DifficultyView, I : DifficultyBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), DifficultyBasePresenter<V, I> {

    override fun submitLoadSegments(moduleId: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val segments = mutableListOf<Segment>()
                val subject = it.fetchSubjectByModule(moduleId)
                subject?.difficulties?.forEach { child ->
                    val difficultTitle = "${subject.title} ${child.title}"
                    val segment = child.markdowns.toSegment(subject.id, difficultTitle)
                    segments.add(segment)
                }
                getView()?.startSegment(segments)
            }
        }
    }

    override fun saveDifficultySelected(difficulty: Difficulty) {
        launchSilent(uiContext) {
            interactor?.let {
                val subjectId = if (difficulty.subject != null) difficulty.subject!!.id else 0
                val subject = it.fetchSubjectBy(subjectId)
                if (subject != null) {
                    it.insertTopicPreferred(TopicPreferred(subject, difficulty))
                }
            }
        }
    }

    override fun submitSelectDifficult(moduleId: Long) {
        launchSilent(uiContext) {
            interactor?.let { it ->
                val subject = it.fetchSubjectByModule(moduleId)
                subject?.let { subIt ->
                    val toolbarTitle = subIt.title
                    getView()?.showDifficulties(subIt.difficulties.withColors(), toolbarTitle)
                }
            }
        }
    }
}