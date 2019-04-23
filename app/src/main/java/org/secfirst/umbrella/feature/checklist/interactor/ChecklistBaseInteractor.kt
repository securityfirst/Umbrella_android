package org.secfirst.umbrella.feature.checklist.interactor

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Content
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor

interface ChecklistBaseInteractor : BaseInteractor {

    suspend fun deleteChecklistContent(checklistContent: Content)

    suspend fun deleteChecklist(checklist: Checklist)

    suspend fun disableChecklistContent(checklistContent: Content)

    suspend fun persistChecklistContent(checklistContent: Content)

    suspend fun persistChecklist(checklist: Checklist)

    suspend fun fetchAllChecklistFavorite(): List<Checklist>

    suspend fun fetchChecklistCount(): Long

    suspend fun fetchAllChecklist(): List<Checklist>

    suspend fun fetchSubjectById(subjectId: String): Subject?

    suspend fun fetchDifficultyById(difficultyId: String): Difficulty?

    suspend fun fetchAllChecklistInProgress(): List<Checklist>

    suspend fun fetchAllCustomChecklistInProgress(): List<Checklist>

    suspend fun fetchChecklist(checklistId: String): Checklist?

    suspend fun fetchModule(moduleName: String): Module?
}