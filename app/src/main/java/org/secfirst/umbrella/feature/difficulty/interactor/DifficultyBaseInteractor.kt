package org.secfirst.umbrella.feature.difficulty.interactor

import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor

interface DifficultyBaseInteractor : BaseInteractor {

    suspend fun insertTopicPreferred(difficultyPreferred: DifficultyPreferred)

    suspend fun fetchSubjectBy(subjectSha1ID: String): Subject?

    suspend fun fetchSubjectByModule(moduleSha1ID: String): Subject?

    suspend fun fetchDifficultyBy(sha1ID : String): Difficulty?

}