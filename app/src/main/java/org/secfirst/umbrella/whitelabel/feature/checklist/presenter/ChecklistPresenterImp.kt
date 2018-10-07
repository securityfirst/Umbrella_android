package org.secfirst.umbrella.whitelabel.feature.checklist.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class ChecklistPresenterImp<V : ChecklistView, I :
ChecklistBaseInteractor> @Inject constructor(interactor: I) :
        BasePresenterImp<V, I>(interactor = interactor), ChecklistBasePresenter<V, I> {

    override fun submitInsertChecklistContent(checklistContent: Content) {
        launchSilent(uiContext) {
            interactor?.persistChecklistContent(checklistContent)
        }
    }

    override fun submitUpdateChecklist(checklist: Checklist) {
        launchSilent(uiContext) {
            interactor?.persistChecklist(checklist)
        }
    }
}