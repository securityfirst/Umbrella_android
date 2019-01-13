package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchModules(): List<Module>

    suspend fun fetchLesson(moduleId: String): Module?

    suspend fun fetchMarkdownBySubject(subjectId: String): List<Markdown>

    suspend fun fetchDifficultyBySubject(subjectId: String): List<Difficulty>

    suspend fun fetchDifficultyPreferredBy(subjectId: String): DifficultyPreferred?

    suspend fun fetchAllFavorites(): List<Markdown>
}