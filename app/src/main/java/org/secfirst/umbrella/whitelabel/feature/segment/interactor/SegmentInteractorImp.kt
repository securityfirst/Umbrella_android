package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.segment.SegmentRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class SegmentInteractorImp @Inject constructor(private val segmentRepo: SegmentRepo) : BaseInteractorImp(), SegmentBaseInteractor {

    override suspend fun fetchCategoryBy(categoryId: Long) = segmentRepo.loadCategoryBy(categoryId)

    override suspend fun fetchSubcategoryBy(subcategoryId: Long) = segmentRepo.loadSubcategoryBy(subcategoryId)
}