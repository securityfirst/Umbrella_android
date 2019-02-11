package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface SegmentBaseInteractor : BaseInteractor {

    suspend fun fetchMarkdown(markdownId: String): Markdown?

    suspend fun fetchChecklist(checklistId: String): Checklist?

    suspend fun fetchSubject(subjectId: String): Subject?

    suspend fun fetchModule(moduleId: String): Module?

    suspend fun fetchModuleByName(moduleName: String): Module?

    suspend fun fetchDifficulty(difficultyId: String): Difficulty?

    suspend fun fetchSubjectByRootDir(rootDir: String): Subject?

    suspend fun fetchDifficultyBySubjectId(subjectId: String): List<Difficulty>

    suspend fun insertChecklist(checklist: Checklist)

    suspend fun insertMarkdown(markdown: Markdown)

    suspend fun insertDifficultySelect(subjectId: String, difficulty: Difficulty)
}