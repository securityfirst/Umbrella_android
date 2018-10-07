package org.secfirst.umbrella.whitelabel.data.database.checklist

import com.raizlabs.android.dbflow.kotlinextensions.save
import javax.inject.Inject

class ChecklistRepository @Inject constructor(private val checklistDao: ChecklistDao) : ChecklistRepo {

    override suspend fun insertChecklist(checklistContent: Content)  = checklistDao.save(checklistContent)

    override suspend fun loadChecklist(id: Long) = checklistDao.getChecklist(id)
}