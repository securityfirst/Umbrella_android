package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun loadMarkdowns(subjectId: Long) = segmentDao.getMarkdowns(subjectId)

    override suspend fun saveMarkdown(markdown: Markdown) = segmentDao.save(markdown)

    override suspend fun saveChecklist(checklist: Checklist) = segmentDao.save(checklist)

    override suspend fun loadModule(moduleId: Long) = segmentDao.getModule(moduleId)

    override suspend fun loadSubject(subjectId: Long) = segmentDao.getSubject(subjectId)
}