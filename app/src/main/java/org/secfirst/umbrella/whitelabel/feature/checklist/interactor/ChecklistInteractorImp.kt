package org.secfirst.umbrella.whitelabel.feature.checklist.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.ChecklistRepo
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChecklistInteractorImp @Inject constructor(private val checklistRepo: ChecklistRepo) : BaseInteractorImp(), ChecklistBaseInteractor {

    override suspend fun persistChecklist(checklist: Checklist)  = checklistRepo.insertChecklist(checklist)

    override suspend fun persistChecklistContent(checklistContent: Content) = checklistRepo.insertChecklistContent(checklistContent)

    override suspend fun fetchChecklistBy(id: Long) = checklistRepo.loadChecklist(id)
}
