package org.secfirst.umbrella.whitelabel.feature.checklist.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

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
}