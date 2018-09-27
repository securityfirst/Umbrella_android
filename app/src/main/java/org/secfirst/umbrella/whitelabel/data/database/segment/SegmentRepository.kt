package org.secfirst.umbrella.whitelabel.data.database.segment

import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun loadCategoryBy(categoryId: Long) = segmentDao.getCategoryBy(categoryId)

    override suspend fun loadSubcategoryBy(subcategoryId: Long) = segmentDao.getSubcategoryBy(subcategoryId)
}