package org.secfirst.umbrella.whitelabel.feature.content.interactor

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface ContentBaseInteractor : BaseInteractor {

    suspend fun fetchData(): Boolean

    suspend fun persist(root: Root)

    suspend fun initParser(): Root

    suspend fun persistFeedSource( feedSources : List<FeedSource>)
}