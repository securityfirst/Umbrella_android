package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject

interface SegmentRepo {

    suspend fun loadMarkdownsFromDifficulty(difficultyId: String): List<Markdown>

    suspend fun loadChecklist(checklistId : String): Checklist?

    suspend fun loadSubject(sha1ID: String): Subject?

    suspend fun loadModule(sha1ID: String): Module?

    suspend fun loadDifficulty(sha1ID: String): Difficulty?

    suspend fun loadMarkdowns(subjectSha1ID: String): List<Markdown>

    suspend fun loadMarkdownsFromModule(moduleId: String): List<Markdown>

    suspend fun saveChecklist(checklist: Checklist)

    suspend fun saveMarkdown(markdown: Markdown)

    suspend fun saveDifficultySelect(subjectSha1ID: String, difficulty: Difficulty)
}