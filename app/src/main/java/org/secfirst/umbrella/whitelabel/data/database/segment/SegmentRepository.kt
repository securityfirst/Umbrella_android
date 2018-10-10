package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun saveMarkdown(markdown: Markdown) = segmentDao.save(markdown)

    override suspend fun saveChecklist(checklist: Checklist) = segmentDao.save(checklist)

    override suspend fun loadCategoryBy(categoryId: Long) = segmentDao.getCategoryBy(categoryId)

    override suspend fun loadSubcategoryBy(subcategoryId: Long) = segmentDao.getSubcategoryBy(subcategoryId)
}