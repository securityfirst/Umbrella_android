package org.secfirst.umbrella.whitelabel.data.database.checklist

import javax.inject.Inject

class ChecklistRepository @Inject constructor(private val checklistDao: ChecklistDao) : ChecklistRepo {

    override suspend fun loadChecklist(checklistId: String) = checklistDao.getChecklist(checklistId)

    override suspend fun loadAllCustomChecklistInProgress() = checklistDao.getAllCustomChecklistInProgress()

    override suspend fun deleteChecklistContent(checklistContent: Content) = checklistDao.deleteChecklistContent(checklistContent)

    override suspend fun deleteChecklist(checklist: Checklist) = checklistDao.deleteChecklist(checklist)

    override suspend fun disableChecklistContent(checklistContent: Content) = checklistDao.disable(checklistContent)

    override suspend fun loadAllChecklistInProgress() = checklistDao.getAllChecklistInProgress()

    override suspend fun loadDifficultyById(difficultyId: String) = checklistDao.getDifficultyById(difficultyId)

    override suspend fun loadSubjectById(subjectId: String) = checklistDao.getSubjectById(subjectId)

    override suspend fun loadAllChecklistFavorite() = checklistDao.getAllChecklistFavorite()

    override suspend fun loadChecklistCount() = checklistDao.getChecklistCount()

    override suspend fun loadAllChecklist() = checklistDao.getAllChecklist()

    override suspend fun insertChecklist(checklist: Checklist) = checklistDao.save(checklist)

    override suspend fun insertChecklistContent(checklistContent: Content) = checklistDao.save(checklistContent)
}