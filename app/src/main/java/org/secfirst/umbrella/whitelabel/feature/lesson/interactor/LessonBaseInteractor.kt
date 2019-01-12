package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchModules(): List<Module>

    suspend fun fetchLesson(sha1ID: String): Module?

    suspend fun fetchSubject(sha1ID: String): Subject?

    suspend fun fetchDifficulty(sha1ID: String): Difficulty?

    suspend fun fetchMarkdownBySubject(subjectSha1ID: String): List<Markdown>

    suspend fun fetchMarkdownByModule(moduleSha1ID : String): Markdown?

    suspend fun fetchMarkdownsBy(sha1ID : String): List<Markdown>

    suspend fun fetchDifficultyBySubject(subjectId: String): List<Difficulty>

    suspend fun fetchDifficultyPreferredBy(subjectSha1ID: String): DifficultyPreferred?

    suspend fun fetchAllFavorites(): List<Markdown>
}