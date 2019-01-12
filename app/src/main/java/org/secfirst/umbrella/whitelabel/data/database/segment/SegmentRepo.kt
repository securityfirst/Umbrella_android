package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject

interface SegmentRepo {

    suspend fun loadMarkdownsFromDifficulty(difficultyId: String): List<Markdown>

    suspend fun loadChecklist(checklistId: String): Checklist?

    suspend fun loadMarkdown(markdownId: String): Markdown?

    suspend fun loadSubject(id: String): Subject?

    suspend fun loadModule(id: String): Module?

    suspend fun loadDifficulty(id: String): Difficulty?

    suspend fun loadMarkdownsFromSubject(subjectSha1ID: String): List<Markdown>

    suspend fun loadMarkdownsFromModule(moduleId: String): List<Markdown>

    suspend fun saveChecklist(checklist: Checklist)

    suspend fun saveMarkdown(markdown: Markdown)

    suspend fun saveDifficultySelect(subjectSha1ID: String, difficulty: Difficulty)
}