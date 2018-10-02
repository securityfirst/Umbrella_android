package org.secfirst.umbrella.whitelabel.data.database.checklist

import javax.inject.Inject

class ChecklistRepository @Inject constructor(private val checklistDao: ChecklistDao) : ChecklistRepo {

    override suspend fun loadChecklist(id: Long) = checklistDao.getChecklist(id)
}