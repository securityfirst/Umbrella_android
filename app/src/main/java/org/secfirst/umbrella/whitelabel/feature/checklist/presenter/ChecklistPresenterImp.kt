package org.secfirst.umbrella.whitelabel.feature.checklist.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class ChecklistPresenterImp<V : ChecklistView, I :
ChecklistBaseInteractor> @Inject constructor(interactor: I) :
        BasePresenterImp<V, I>(interactor = interactor), ChecklistBasePresenter<V, I> {

    override fun submitInsertChecklist(checklistContent: Content) {
        launchSilent(uiContext) {
            interactor?.persistChecklist(checklistContent)
        }
    }

    override fun submitLoadChecklist(selectDifficulty: Difficulty) {

    }

}