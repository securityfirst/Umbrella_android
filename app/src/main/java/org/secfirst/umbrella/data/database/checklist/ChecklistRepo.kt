package org.secfirst.umbrella.data.database.checklist

import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject

interface ChecklistRepo {

    suspend fun deleteChecklistContent(checklistContent: Content)

    suspend fun deleteChecklist(checklist: Checklist)

    suspend fun disableChecklistContent(checklistContent: Content)

    suspend fun insertChecklistContent(checklistContent: Content)

    suspend fun insertChecklist(checklist: Checklist)

    suspend fun loadAllChecklistFavorite(): List<Checklist>

    suspend fun loadChecklistCount(): Long

    suspend fun loadAllChecklist(): List<Checklist>

    suspend fun loadSubjectById(subjectId: String): Subject?

    suspend fun loadDifficultyById(difficultyId: String): Difficulty?

    suspend fun loadAllChecklistInProgress(): List<Checklist>

    suspend fun loadAllCustomChecklistInProgress(): List<Checklist>

    suspend fun loadChecklist(checklistId: String): Checklist?

    suspend fun loadModule(moduleName: String): Module?

    suspend fun loadAllPathways(): List<Checklist>

    suspend fun loadFavoritePathways() : List<Checklist>
}