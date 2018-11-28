package org.secfirst.umbrella.whitelabel.data.database.segment

import com.raizlabs.android.dbflow.kotlinextensions.save
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun saveDifficultySelect(subjectSha1ID: String, difficulty: Difficulty) = segmentDao.save(subjectSha1ID, difficulty)

    override suspend fun loadMarkdowns(subjectSha1ID: String) = segmentDao.getMarkdowns(subjectSha1ID)

    override suspend fun saveMarkdown(markdown: Markdown) = segmentDao.save(markdown)

    override suspend fun saveChecklist(checklist: Checklist) = segmentDao.save(checklist)

    override suspend fun loadModule(sha1ID: String) = segmentDao.getModule(sha1ID)

    override suspend fun loadSubject(sha1ID: String) = segmentDao.getSubject(sha1ID)
}