package org.secfirst.umbrella.whitelabel.data.database.lesson

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown

interface LessonRepo {

    suspend fun loadAllModules(): List<Module>

    suspend fun loadSubject(sha1ID: String): Subject?

    suspend fun loadLessonBy(sha1ID: String): Module?

    suspend fun loadMarkdownBySubject(sha1ID: String): List<Markdown>

    suspend fun loadMarkdownByModule(moduleSh1ID: String): Markdown?

    suspend fun loadMarkdownsBy(sha1ID: String): List<Markdown>

    suspend fun loadDifficultyBy(sha1ID: String): Difficulty?

    suspend fun loadAllFavoriteSubjects(): List<Markdown>

    suspend fun loadDifficultyPreferredBy(subjectSha1ID : String): DifficultyPreferred?

}