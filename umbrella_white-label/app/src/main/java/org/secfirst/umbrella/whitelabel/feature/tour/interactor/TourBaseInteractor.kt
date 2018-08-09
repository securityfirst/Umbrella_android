package org.secfirst.umbrella.whitelabel.feature.tour.interactor

import org.secfirst.umbrella.whitelabel.data.Root
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface TourBaseInteractor : BaseInteractor {

    suspend fun fetchData(): Boolean

    suspend fun persist(root: Root)

    suspend fun initParser(): Root
}