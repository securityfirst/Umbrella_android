package org.secfirst.umbrella.whitelabel.data.database.checklist

interface ChecklistRepo {

    suspend fun loadChecklist(id: Long): Checklist?
}