package org.secfirst.umbrella.feature.lesson.interactor

import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchModules(): List<Module>

    suspend fun fetchLesson(moduleId: String): Module?

    suspend fun fetchMarkdownBySubject(subjectId: String): List<Markdown>

    suspend fun fetchDifficultyBySubject(subjectId: String): List<Difficulty>

    suspend fun fetchDifficultyPreferredBy(subjectId: String): DifficultyPreferred?

    suspend fun fetchAllFavorites(): List<Markdown>
}