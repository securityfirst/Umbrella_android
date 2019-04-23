package org.secfirst.umbrella.feature.difficulty.interactor

import org.secfirst.umbrella.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.data.database.difficulty.DifficultyRepo
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class DifficultyInteractorImp @Inject constructor(private val difficultyRepo: DifficultyRepo) : BaseInteractorImp(), DifficultyBaseInteractor {


    override suspend fun fetchSubjectByModule(moduleSha1ID : String) = difficultyRepo.loadSubjectByModule(moduleSha1ID)

    override suspend fun fetchDifficultyBy(sha1ID : String) = difficultyRepo.loadDifficultyBy(sha1ID )

    override suspend fun insertTopicPreferred(difficultyPreferred: DifficultyPreferred) = difficultyRepo.saveTopicPreferred(difficultyPreferred)

    override suspend fun fetchSubjectBy(subjectSha1ID : String) = difficultyRepo.loadSubjectBy(subjectSha1ID)

}
