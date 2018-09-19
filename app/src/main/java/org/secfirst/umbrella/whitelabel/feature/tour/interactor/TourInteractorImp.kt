package org.secfirst.umbrella.whitelabel.feature.tour.interactor

import org.secfirst.umbrella.whitelabel.data.Root
import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.disk.TentRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import org.secfirst.umbrella.whitelabel.serialize.ElementLoader
import org.secfirst.umbrella.whitelabel.serialize.ElementSerializer
import javax.inject.Inject

class TourInteractorImp @Inject constructor(apiHelper: ApiHelper,
                                            private val tentRepo: TentRepo,
                                            private val contentRepo: ContentRepo,
                                            private val elementSerializer: ElementSerializer,
                                            private val elementLoader: ElementLoader)

    : BaseInteractorImp(apiHelper), TourBaseInteractor {

    override suspend fun fetchData() = tentRepo.fetch()

    override suspend fun initParser() = elementLoader.load(elementSerializer.serialize())

    override suspend fun persist(root: Root) = contentRepo.insertAllLessons(root)

}