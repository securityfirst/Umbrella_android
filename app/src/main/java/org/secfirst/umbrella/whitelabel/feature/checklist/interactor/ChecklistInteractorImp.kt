package org.secfirst.umbrella.whitelabel.feature.checklist.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.ChecklistRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChecklistInteractorImp @Inject constructor(private val checklistRepo: ChecklistRepo) : BaseInteractorImp(), ChecklistBaseInteractor {

    override suspend fun fetchChecklistBy(id: Long) = checklistRepo.loadChecklist(id)
}
