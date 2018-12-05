package org.secfirst.umbrella.whitelabel.data.database.checklist

import javax.inject.Inject

class ChecklistRepository @Inject constructor(private val checklistDao: ChecklistDao) : ChecklistRepo {

    override suspend fun loadCustomChecklistCount() = checklistDao.getCustomChecklistCount()

    override suspend fun getAllCustomChecklistInProgress() = checklistDao.getAllCustomChecklistInProgress()

    override suspend fun delteChecklistContent(checklistContent: Content) = checklistDao.deleteChecklistContent(checklistContent)

    override suspend fun disableChecklistContent(checklistContent: Content) = checklistDao.disable(checklistContent)

    override suspend fun getAllChecklistInProgress(): List<Checklist> = checklistDao.getAllChecklistInProgress()

    override suspend fun loadDifficultyById(sha1ID: String) = checklistDao.getDifficultyById(sha1ID)

    override suspend fun loadSubjectById(subjectSha1ID: String) = checklistDao.getSubjectById(subjectSha1ID)

    override suspend fun loadAllChecklistFavorite() = checklistDao.getAllChecklistFavorite()

    override suspend fun loadChecklistCount() = checklistDao.getChecklistCount()

    override suspend fun loadAllChecklist() = checklistDao.getAllChecklist()

    override suspend fun loadChecklistProgressDone(): List<Checklist> = checklistDao.getChecklistProgressDone()

    override suspend fun insertChecklist(checklist: Checklist) = checklistDao.save(checklist)

    override suspend fun insertChecklistContent(checklistContent: Content) = checklistDao.save(checklistContent)

    override suspend fun loadChecklist(sha1ID: String) = checklistDao.getChecklist(sha1ID)
}