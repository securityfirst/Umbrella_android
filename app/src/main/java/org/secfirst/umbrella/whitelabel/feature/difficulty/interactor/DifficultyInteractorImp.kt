package org.secfirst.umbrella.whitelabel.feature.difficulty.interactor

import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class DifficultyInteractorImp @Inject constructor(private val difficultyRepo: DifficultyRepo) : BaseInteractorImp(), DifficultyBaseInteractor {


    override suspend fun fetchSubjectByModule(moduleSha1ID : String) = difficultyRepo.loadSubjectByModule(moduleSha1ID)

    override suspend fun fetchDifficultyBy(id: Long) = difficultyRepo.loadChildBy(id)

    override suspend fun insertTopicPreferred(difficultyPreferred: DifficultyPreferred) = difficultyRepo.saveTopicPreferred(difficultyPreferred)

    override suspend fun fetchSubjectBy(subjectSha1ID : String) = difficultyRepo.loadSubjectBy(subjectSha1ID)

}
