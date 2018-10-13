package org.secfirst.umbrella.whitelabel.feature.difficulty.interactor

import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyRepo
import org.secfirst.umbrella.whitelabel.data.database.difficulty.TopicPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class DifficultyInteractorImp @Inject constructor(private val difficultyRepo: DifficultyRepo) : BaseInteractorImp(), DifficultyBaseInteractor {


    override suspend fun fetchSubjectByModule(moduleId: Long) = difficultyRepo.loadSubjectByModule(moduleId)

    override suspend fun fetchDifficultyBy(id: Long) = difficultyRepo.loadChildBy(id)

    override suspend fun insertTopicPreferred(topicPreferred: TopicPreferred) = difficultyRepo.saveTopicPreferred(topicPreferred)

    override suspend fun fetchSubjectBy(subjectId: Long) = difficultyRepo.loadSubjectBy(subjectId)

}
