package org.secfirst.umbrella.whitelabel.data.database.checklist

import javax.inject.Inject

class ChecklistRepository @Inject constructor(private val checklistDao: ChecklistDao) : ChecklistRepo {

    override suspend fun insertChecklist(checklist: Checklist) = checklistDao.save(checklist)

    override suspend fun insertChecklistContent(checklistContent: Content) = checklistDao.save(checklistContent)

    override suspend fun loadChecklist(id: Long) = checklistDao.getChecklist(id)
}