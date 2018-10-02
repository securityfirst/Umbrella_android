package org.secfirst.umbrella.whitelabel.feature.checklist.presenter

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor

interface ChecklistBasePresenter<V : ChecklistView, I : ChecklistBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadChecklist(selectDifficulty: Difficulty)
}