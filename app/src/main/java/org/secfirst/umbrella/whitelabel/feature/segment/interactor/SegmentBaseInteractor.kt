package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface  SegmentBaseInteractor : BaseInteractor{
    suspend fun fetchSubcategoryBy(categoryId: Long): Subcategory
}