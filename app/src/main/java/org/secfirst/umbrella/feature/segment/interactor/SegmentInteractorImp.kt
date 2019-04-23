package org.secfirst.umbrella.feature.segment.interactor

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.SegmentRepo
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class SegmentInteractorImp @Inject constructor(private val segmentRepo: SegmentRepo) : BaseInteractorImp(), SegmentBaseInteractor {

    override suspend fun fetchSubjectByRootDir(rootDir: String) = segmentRepo.loadSubjectByRootDir(rootDir)

    override suspend fun fetchDifficultyBySubject(subjectId: String) =
            segmentRepo.loadDifficultyBySubjectId(subjectId)

    override suspend fun fetchModuleByRootDir(rootDir: String) = segmentRepo.loadModuleByRootdir(rootDir)

    override suspend fun fetchMarkdown(markdownId: String) = segmentRepo.loadMarkdown(markdownId)

    override suspend fun fetchChecklist(checklistId: String) = segmentRepo.loadChecklist(checklistId)

    override suspend fun fetchDifficulty(difficultyId: String) = segmentRepo.loadDifficulty(difficultyId)

    override suspend fun insertDifficultySelect(subjectId: String, difficulty: Difficulty) =
            segmentRepo.saveDifficultySelect(subjectId, difficulty)

    override suspend fun insertMarkdown(markdown: Markdown) = segmentRepo.saveMarkdown(markdown)

    override suspend fun insertChecklist(checklist: Checklist) = segmentRepo.saveChecklist(checklist)

    override suspend fun fetchModule(moduleId: String) = segmentRepo.loadModule(moduleId)

    override suspend fun fetchSubject(subjectId: String) = segmentRepo.loadSubject(subjectId)
}