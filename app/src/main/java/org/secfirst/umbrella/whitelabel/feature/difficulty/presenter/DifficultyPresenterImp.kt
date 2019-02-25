package org.secfirst.umbrella.whitelabel.feature.difficulty.presenter

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.difficulty.ids
import org.secfirst.umbrella.whitelabel.data.database.difficulty.withColors
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class DifficultyPresenterImp<V : DifficultyView, I : DifficultyBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), DifficultyBasePresenter<V, I> {

    override fun submitDifficultySelect(difficulties: List<Difficulty>) {
        getView()?.startSegment(difficulties.ids())
    }

    override fun saveDifficultySelect(difficulty: Difficulty, subjectId: String) {
        launchSilent(uiContext) {
            interactor?.insertTopicPreferred(DifficultyPreferred(subjectId, difficulty))
        }
    }

    override fun submitDifficulty(subjectId: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val subject = it.fetchSubjectBy(subjectId)
                if (subject != null && subject.difficulties.isNotEmpty()) {
                    val toolbarTitle = subject.title
                    getView()?.showDifficulties(subject.difficulties.withColors(), toolbarTitle)
                }
            }
        }
    }
}