package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun loadMarkdown(markdownId: String) = segmentDao.getMarkdown(markdownId)

    override suspend fun loadMarkdownsFromDifficulty(difficultyId: String) = segmentDao.getMarkdownFromDifficulty(difficultyId)

    override suspend fun loadChecklist(checklistId: String) = segmentDao.getChecklist(checklistId)

    override suspend fun loadMarkdownsFromModule(moduleId: String) = segmentDao.getMarkdownFromModule(moduleId)

    override suspend fun loadDifficulty(id: String) = segmentDao.getDifficulty(id)

    override suspend fun saveDifficultySelect(subjectSha1ID: String, difficulty: Difficulty) = segmentDao.save(subjectSha1ID, difficulty)

    override suspend fun loadMarkdownsFromSubject(subjectSha1ID: String) = segmentDao.getMarkdownFromSubject(subjectSha1ID)

    override suspend fun saveMarkdown(markdown: Markdown) = segmentDao.save(markdown)

    override suspend fun saveChecklist(checklist: Checklist) = segmentDao.save(checklist)

    override suspend fun loadModule(id: String) = segmentDao.getModule(id)

    override suspend fun loadSubject(id: String) = segmentDao.getSubject(id)
}