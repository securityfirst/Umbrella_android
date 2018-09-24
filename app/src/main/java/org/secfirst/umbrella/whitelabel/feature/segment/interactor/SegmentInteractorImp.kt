package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.segment.SegmentRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class SegmentInteractorImp @Inject constructor(private val segmentRepo: SegmentRepo) : BaseInteractorImp(), SegmentBaseInteractor {

    override suspend fun fetchSubcategoryBy(categoryId: Long) = segmentRepo.loadSubcategoryBy(categoryId)
}