package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject

interface SegmentRepo {
    suspend fun loadSubject(subjectId: Long): Subject?

    suspend fun loadModule(moduleId: Long): Module?

    suspend fun loadMarkdowns(subjectId: Long): List<Markdown>

    suspend fun loadDifficultyChild(difficulties: List<Difficulty>): List<Difficulty>

    suspend fun saveChecklist(checklist: Checklist)

    suspend fun saveMarkdown(markdown: Markdown)

    suspend fun saveDifficultySelect(subjectId: Long, difficulty: Difficulty)
}