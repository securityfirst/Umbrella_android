package org.secfirst.umbrella.whitelabel.feature.difficulty.interactor

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface DifficultyBaseInteractor : BaseInteractor {

    suspend fun insertTopicPreferred(difficultyPreferred: DifficultyPreferred)

    suspend fun fetchSubjectBy(subjectId: Long): Subject?

    suspend fun fetchSubjectByModule(moduleId: Long): Subject?

    suspend fun fetchDifficultyBy(id: Long): Difficulty?

}