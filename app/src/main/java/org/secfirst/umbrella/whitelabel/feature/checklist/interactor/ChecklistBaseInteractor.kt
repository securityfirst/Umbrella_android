package org.secfirst.umbrella.whitelabel.feature.checklist.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface ChecklistBaseInteractor : BaseInteractor {

    suspend fun fetchChecklistBy(sha1ID: String): Checklist?

    suspend fun fetchChecklistProgressDone(): List<Checklist>

    suspend fun deleteChecklistContent(checklistContent: Content)

    suspend fun disableChecklistContent(checklistContent: Content)

    suspend fun persistChecklistContent(checklistContent: Content)

    suspend fun persistChecklist(checklist: Checklist)

    suspend fun fetchAllChecklistFavorite(): List<Checklist>

    suspend fun fetchChecklistCount(): Long

    suspend fun fetchAllChecklist(): List<Checklist>

    suspend fun fetchSubjectById(subjectSha1ID: String): Subject?

    suspend fun fetchDifficultyById(sha1ID: String): Difficulty

    suspend fun fetchAllChecklistInProgress(): List<Checklist>
}