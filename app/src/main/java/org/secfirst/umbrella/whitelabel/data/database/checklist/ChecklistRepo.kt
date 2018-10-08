package org.secfirst.umbrella.whitelabel.data.database.checklist

import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty

interface ChecklistRepo {

    suspend fun loadChecklist(id: Long): Checklist?

    suspend fun insertChecklistContent(checklistContent: Content)

    suspend fun insertChecklist(checklist: Checklist)

    suspend fun loadChecklistProgressDone(): List<Checklist>

    suspend fun loadAllChecklistFavorite(): List<Checklist>

    suspend fun loadChecklistCount(): Long

    suspend fun loadAllChecklist(): List<Checklist>

    suspend fun loadSubjectById(subjectId: Long): Subject?

    suspend fun loadDifficultyById(difficultyId: Long): Difficulty

    suspend fun getAllChecklistInProgress(): List<Checklist>
}