package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.SegmentRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class SegmentInteractorImp @Inject constructor(private val segmentRepo: SegmentRepo) : BaseInteractorImp(), SegmentBaseInteractor {

    override suspend fun fetchMarkdown(markdownId: String) = segmentRepo.loadMarkdown(markdownId)

    override suspend fun fetchMarkdownsFromDifficulty(difficultyId: String) = segmentRepo.loadMarkdownsFromDifficulty(difficultyId)

    override suspend fun fetchChecklist(checklistId: String) = segmentRepo.loadChecklist(checklistId)

    override suspend fun fetchMarkdownsFromModule(moduleId: String) = segmentRepo.loadMarkdownsFromModule(moduleId)

    override suspend fun fetchDifficulty(difficultyId: String) = segmentRepo.loadDifficulty(difficultyId)

    override suspend fun insertDifficultySelect(subjectSha1ID: String, difficulty: Difficulty) = segmentRepo.saveDifficultySelect(subjectSha1ID, difficulty)

    override suspend fun fetchMarkdownsFromSubject(subjectSha1ID: String) = segmentRepo.loadMarkdownsFromSubject(subjectSha1ID)

    override suspend fun insertMarkdown(markdown: Markdown) = segmentRepo.saveMarkdown(markdown)

    override suspend fun insertChecklist(checklist: Checklist) = segmentRepo.saveChecklist(checklist)

    override suspend fun fetchModule(sha1ID: String) = segmentRepo.loadModule(sha1ID)

    override suspend fun fetchSubject(sha1ID: String) = segmentRepo.loadSubject(sha1ID)
}