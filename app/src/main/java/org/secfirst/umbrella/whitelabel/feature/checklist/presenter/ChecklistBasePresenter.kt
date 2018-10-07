package org.secfirst.umbrella.whitelabel.feature.checklist.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView

interface ChecklistBasePresenter<V : ChecklistView, I : ChecklistBaseInteractor> : BasePresenter<V, I> {

    fun submitInsertChecklistContent(checklistContent: Content)

    fun submitUpdateChecklist(checklist: Checklist)
}