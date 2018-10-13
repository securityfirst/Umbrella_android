package org.secfirst.umbrella.whitelabel.feature.difficulty.interactor

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.TopicPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface DifficultyBaseInteractor : BaseInteractor {

    suspend fun insertTopicPreferred(topicPreferred: TopicPreferred)

    suspend fun fetchSubjectBy(subjectId: Long): Subject?

    suspend fun fetchSubjectByModule(moduleId: Long): Subject?

    suspend fun fetchDifficultyBy(id: Long): Difficulty?

}