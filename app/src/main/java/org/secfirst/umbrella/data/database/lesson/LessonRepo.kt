package org.secfirst.umbrella.data.database.lesson

import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.data.database.segment.Markdown

interface LessonRepo {

    suspend fun loadAllModules(): List<Module>

    suspend fun loadLessonBy(moduleId: String): Module?

    suspend fun loadMarkdownBySubject(subjectId: String): List<Markdown>

    suspend fun loadDifficultyBySubject(subjectId: String): List<Difficulty>

    suspend fun loadAllFavoriteSubjects(): List<Markdown>

    suspend fun loadDifficultyPreferredBy(subjectId: String): DifficultyPreferred?

}