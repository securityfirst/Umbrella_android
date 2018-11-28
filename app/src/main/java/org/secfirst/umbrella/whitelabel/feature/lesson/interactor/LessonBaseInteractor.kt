package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchModules(): List<Module>

    suspend fun fetchLesson(id: Long): Module?

    suspend fun fetchSubject(id: Long): Subject?

    suspend fun fetchDifficulty(id: Long): Difficulty?

    suspend fun fetchMarkdownBySubject(subjectId: Long): List<Markdown>

    suspend fun fetchMarkdownByModule(moduleId: Long): Markdown?

    suspend fun fetchMarkdownsBy(sha1ID : String): List<Markdown>

    suspend fun fetchDifficultyPreferredBy(subjectId : Long): DifficultyPreferred?

    suspend fun fetchAllFavorites(): List<Markdown>
}