package org.secfirst.umbrella.whitelabel.feature.checklist.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.ChecklistRepo
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ChecklistInteractorImp @Inject constructor(private val checklistRepo: ChecklistRepo) : BaseInteractorImp(), ChecklistBaseInteractor {

    override suspend fun fetchCustomChecklistCount() = checklistRepo.loadCustomChecklistCount()

    override suspend fun fetchAllCustomChecklistInProgress() = checklistRepo.getAllCustomChecklistInProgress()

    override suspend fun deleteChecklistContent(checklistContent: Content) = checklistRepo.delteChecklistContent(checklistContent)

    override suspend fun deleteChecklist(checklist: Checklist) = checklistRepo.deleteChecklist(checklist)

    override suspend fun disableChecklistContent(checklistContent: Content) = checklistRepo.disableChecklistContent(checklistContent)

    override suspend fun fetchAllChecklistInProgress() = checklistRepo.getAllChecklistInProgress()

    override suspend fun fetchDifficultyById(sha1ID: String) = checklistRepo.loadDifficultyById(sha1ID)

    override suspend fun fetchSubjectById(subjectSha1ID: String) = checklistRepo.loadSubjectById(subjectSha1ID)

    override suspend fun fetchAllChecklistFavorite() = checklistRepo.loadAllChecklistFavorite()

    override suspend fun fetchChecklistCount() = checklistRepo.loadChecklistCount()

    override suspend fun fetchAllChecklist() = checklistRepo.loadAllChecklist()

    override suspend fun fetchChecklistProgressDone() = checklistRepo.loadChecklistProgressDone()

    override suspend fun persistChecklist(checklist: Checklist) = checklistRepo.insertChecklist(checklist)

    override suspend fun persistChecklistContent(checklistContent: Content) = checklistRepo.insertChecklistContent(checklistContent)

    override suspend fun fetchChecklistBy(sha1ID: String) = checklistRepo.loadChecklist(sha1ID)
}
