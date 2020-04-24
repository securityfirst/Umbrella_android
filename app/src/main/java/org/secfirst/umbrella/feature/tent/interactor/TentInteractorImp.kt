package org.secfirst.umbrella.feature.tent.interactor

import org.secfirst.umbrella.data.disk.TentRepo
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class TentInteractorImp @Inject constructor(private val tentRepo: TentRepo)
    : BaseInteractorImp(), TentBaseInteractor {

    override suspend fun updateRepository() = tentRepo.updateRepository()

    override suspend fun fetchRepository(url: String) = tentRepo.fetchRepository(url)

    override suspend fun loadElementsFile(path : String) = tentRepo.loadElementsFile(path)
}