package org.secfirst.umbrella.whitelabel.feature.checklist.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.ChecklistRepo
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChecklistInteractorImp @Inject constructor(private val checklistRepo: ChecklistRepo) : BaseInteractorImp(), ChecklistBaseInteractor {

    override suspend fun fetchAllChecklistInProgress() = checklistRepo.getAllChecklistInProgress()

    override suspend fun fetchDifficultyById(difficultyId: Long) = checklistRepo.loadDifficultyById(difficultyId)

    override suspend fun fetchSubjectById(subjectId: Long) = checklistRepo.loadSubjectById(subjectId)

    override suspend fun fetchAllChecklistFavorite() = checklistRepo.loadAllChecklistFavorite()

    override suspend fun fetchChecklistCount() = checklistRepo.loadChecklistCount()

    override suspend fun fetchAllChecklist() = checklistRepo.loadAllChecklist()

    override suspend fun fetchChecklistProgressDone() = checklistRepo.loadChecklistProgressDone()

    override suspend fun persistChecklist(checklist: Checklist) = checklistRepo.insertChecklist(checklist)

    override suspend fun persistChecklistContent(checklistContent: Content) = checklistRepo.insertChecklistContent(checklistContent)

    override suspend fun fetchChecklistBy(id: Long) = checklistRepo.loadChecklist(id)
}
