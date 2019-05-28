package org.secfirst.umbrella.feature.checklist.interactor

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.ChecklistRepo
import org.secfirst.umbrella.data.database.checklist.Content
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChecklistInteractorImp @Inject constructor(private val checklistRepo: ChecklistRepo) : BaseInteractorImp(), ChecklistBaseInteractor {
    override suspend fun fetchModule(moduleName: String) = checklistRepo.loadModule(moduleName)

    override suspend fun fetchChecklist(checklistId: String) = checklistRepo.loadChecklist(checklistId)

    override suspend fun fetchAllCustomChecklistInProgress() = checklistRepo.loadAllCustomChecklistInProgress()

    override suspend fun deleteChecklistContent(checklistContent: Content) = checklistRepo.deleteChecklistContent(checklistContent)

    override suspend fun deleteChecklist(checklist: Checklist) = checklistRepo.deleteChecklist(checklist)

    override suspend fun disableChecklistContent(checklistContent: Content) = checklistRepo.disableChecklistContent(checklistContent)

    override suspend fun fetchAllChecklistInProgress() = checklistRepo.loadAllChecklistInProgress()

    override suspend fun fetchDifficultyById(difficultyId: String) = checklistRepo.loadDifficultyById(difficultyId)

    override suspend fun fetchSubjectById(subjectId: String) = checklistRepo.loadSubjectById(subjectId)

    override suspend fun fetchAllChecklistFavorite() = checklistRepo.loadAllChecklistFavorite()

    override suspend fun fetchChecklistCount() = checklistRepo.loadChecklistCount()

    override suspend fun fetchAllChecklist() = checklistRepo.loadAllChecklist()

    override suspend fun persistChecklist(checklist: Checklist) = checklistRepo.insertChecklist(checklist)

    override suspend fun persistChecklistContent(checklistContent: Content) = checklistRepo.insertChecklistContent(checklistContent)

    override suspend fun fetchPathways(): List<Checklist> = checklistRepo.loadAllPathways()

    override suspend fun fetchFavoritePathways(): List<Checklist> = checklistRepo.loadFavoritePathways()
}
