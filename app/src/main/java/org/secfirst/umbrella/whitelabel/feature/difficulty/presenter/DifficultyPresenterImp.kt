package org.secfirst.umbrella.whitelabel.feature.difficulty.presenter

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.difficulty.withColors
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import org.secfirst.umbrella.whitelabel.misc.runBlockingSilent
import javax.inject.Inject

class DifficultyPresenterImp<V : DifficultyView, I : DifficultyBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), DifficultyBasePresenter<V, I> {

    override fun submitSelectedDifficulty(selectDifficulty: Difficulty) {
        getView()?.startSegment(selectDifficulty)
    }

    override fun saveDifficultySelect(difficulty: Difficulty, subjectSha1ID: String) {
        runBlockingSilent(uiContext) {
            interactor?.insertTopicPreferred(DifficultyPreferred(subjectSha1ID, difficulty))
        }
    }

    override fun submitDifficulty(subject: Subject) {
        runBlockingSilent(uiContext) {
            interactor?.let {
                val toolbarTitle = subject.title
                getView()?.showDifficulties(subject.difficulties.withColors(), toolbarTitle)
            }
        }
    }
}