package org.secfirst.umbrella.whitelabel.data.database.checklist

import javax.inject.Inject

class ChecklistRepository @Inject constructor(private val checklistDao: ChecklistDao) : ChecklistRepo {

    override suspend fun getAllChecklistInProgress(): List<Checklist> = checklistDao.getAllChecklistInProgress()

    override suspend fun loadDifficultyById(difficultyId: Long) = checklistDao.getDifficultyById(difficultyId)

    override suspend fun loadSubjectById(subjectId: Long) = checklistDao.getSubjectById(subjectId)

    override suspend fun loadAllChecklistFavorite() = checklistDao.getAllChecklistFavorite()

    override suspend fun loadChecklistCount() = checklistDao.getChecklistCount()

    override suspend fun loadAllChecklist() = checklistDao.getAllChecklist()

    override suspend fun loadChecklistProgressDone(): List<Checklist> = checklistDao.getChecklistProgressDone()

    override suspend fun insertChecklist(checklist: Checklist) = checklistDao.save(checklist)

    override suspend fun insertChecklistContent(checklistContent: Content) = checklistDao.save(checklistContent)

    override suspend fun loadChecklist(id: Long) = checklistDao.getChecklist(id)
}