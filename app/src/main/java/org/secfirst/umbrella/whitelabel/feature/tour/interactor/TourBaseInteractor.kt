package org.secfirst.umbrella.whitelabel.feature.tour.interactor

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface TourBaseInteractor : BaseInteractor {

    suspend fun fetchData(): Boolean

    suspend fun persist(root: Root)

    suspend fun initParser(): Root

    suspend fun persistFeedSource(feedSources: List<FeedSource>)

    suspend fun initDatabase(userToken: String) : Boolean
}