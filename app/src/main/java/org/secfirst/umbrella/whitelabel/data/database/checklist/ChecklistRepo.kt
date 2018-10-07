package org.secfirst.umbrella.whitelabel.data.database.checklist

interface ChecklistRepo {

    suspend fun loadChecklist(id: Long): Checklist?

    suspend fun insertChecklistContent(checklistContent: Content)

    suspend fun insertChecklist(checklist: Checklist)
}