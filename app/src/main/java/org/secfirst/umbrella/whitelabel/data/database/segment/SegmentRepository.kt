package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import javax.inject.Inject

class SegmentRepository @Inject constructor(private val segmentDao: SegmentDao) : SegmentRepo {

    override suspend fun loadSubjectByRootDir(rootDir: String) = segmentDao.getSubjectByRootDir(rootDir)

    override suspend fun loadDifficultyBySubjectId(subjectId: String) = segmentDao.getDifficultyBySubjectId(subjectId)

    override suspend fun loadModuleByRootdir(moduleName: String) = segmentDao.getModuleByRootDir(moduleName)

    override suspend fun loadMarkdown(markdownId: String) = segmentDao.getMarkdown(markdownId)

    override suspend fun loadChecklist(checklistId: String) = segmentDao.getChecklist(checklistId)

    override suspend fun loadDifficulty(difficultyId: String) = segmentDao.getDifficulty(difficultyId)

    override suspend fun saveDifficultySelect(subjectId: String, difficulty: Difficulty) = segmentDao.save(subjectId, difficulty)

    override suspend fun saveMarkdown(markdown: Markdown) = segmentDao.save(markdown)

    override suspend fun saveChecklist(checklist: Checklist) = segmentDao.save(checklist)

    override suspend fun loadModule(moduleId: String) = segmentDao.getModule(moduleId)

    override suspend fun loadSubject(subjectId: String) = segmentDao.getSubject(subjectId)
}