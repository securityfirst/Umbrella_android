package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun loadMarkdownsFromDifficulty(difficultyId: String) = segmentDao.getMarkdownFromDifficulty(difficultyId)

    override suspend fun loadChecklist(checklistId: String) = segmentDao.getChecklist(checklistId)

    override suspend fun loadMarkdownsFromModule(moduleId: String) = segmentDao.getMarkdownBy(moduleId)

    override suspend fun loadDifficulty(sha1ID: String) = segmentDao.getDifficulty(sha1ID)

    override suspend fun saveDifficultySelect(subjectSha1ID: String, difficulty: Difficulty) = segmentDao.save(subjectSha1ID, difficulty)

    override suspend fun loadMarkdowns(subjectSha1ID: String) = segmentDao.getMarkdowns(subjectSha1ID)

    override suspend fun saveMarkdown(markdown: Markdown) = segmentDao.save(markdown)

    override suspend fun saveChecklist(checklist: Checklist) = segmentDao.save(checklist)

    override suspend fun loadModule(sha1ID: String) = segmentDao.getModule(sha1ID)

    override suspend fun loadSubject(sha1ID: String) = segmentDao.getSubject(sha1ID)
}