package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface SegmentBaseInteractor : BaseInteractor {

    suspend fun fetchMarkdownsFromDifficulty(difficultyId: String): List<Markdown>

    suspend fun fetchChecklist(checklistId: String): Checklist?

    suspend fun fetchSubject(sha1ID: String): Subject?

    suspend fun fetchModule(sha1ID: String): Module?

    suspend fun fetchDifficulty(difficultyId: String): Difficulty?

    suspend fun fetchMarkdowns(subjectSha1ID: String): List<Markdown>

    suspend fun fetchMarkdownsFromModule(moduleId: String): List<Markdown>

    suspend fun insertChecklist(checklist: Checklist)

    suspend fun insertMarkdown(markdown: Markdown)

    suspend fun insertDifficultySelect(subjectSha1ID: String, difficulty: Difficulty)
}