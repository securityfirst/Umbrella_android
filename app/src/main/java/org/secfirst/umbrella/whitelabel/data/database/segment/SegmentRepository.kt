package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun loadSubcategoryBy(categoryId: Long): Subcategory = segmentDao.getSubcategoryBy(categoryId)

}