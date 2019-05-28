package org.secfirst.umbrella.feature.checklist.presenter

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Content
import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.feature.checklist.view.ChecklistView

interface ChecklistBasePresenter<V : ChecklistView, I : ChecklistBaseInteractor> : BasePresenter<V, I> {

    fun submitChecklistById(uriString: String)

    fun submitInsertChecklistContent(checklistContent: Content)

    fun submitUpdateChecklist(checklist: Checklist)

    fun submitDeleteChecklistContent(checklistContent: Content)

    fun submitDisableChecklistContent(checklistContent: Content)

    fun submitLoadDashboard()

    fun submitInsertCustomChecklist(checklistTitle: String, checklistId: String, checklistValue: List<String>)

    fun submitLoadCustomDashboard()

    fun submitDeleteChecklist(checklist: Checklist)

    fun submitChecklist(checklistId: String)

    fun submitLoadPathways()
}