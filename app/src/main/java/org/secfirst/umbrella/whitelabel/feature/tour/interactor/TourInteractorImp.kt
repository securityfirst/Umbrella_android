package org.secfirst.umbrella.whitelabel.feature.tour.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepo
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.data.disk.TentRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import org.secfirst.umbrella.whitelabel.serialize.ElementLoader
import org.secfirst.umbrella.whitelabel.serialize.ElementSerialize
import javax.inject.Inject

class TourInteractorImp @Inject constructor(apiHelper: ApiHelper,
                                            private val tentRepo: TentRepo,
                                            private val contentRepo: ContentRepo,
                                            private val elementSerialize: ElementSerialize,
                                            private val elementLoader: ElementLoader)
    : BaseInteractorImp(apiHelper), TourBaseInteractor {

    override suspend fun persistFeedSource(feedSources: List<FeedSource>) = contentRepo.insertFeedSource(feedSources)

    override suspend fun fetchData() = tentRepo.fetch()

    override suspend fun initParser() = elementLoader.load(elementSerialize.process())

    override suspend fun persist(root: Root) = contentRepo.insertAllLessons(root)

}