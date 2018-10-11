package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.SegmentRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class SegmentInteractorImp @Inject constructor(private val segmentRepo: SegmentRepo) : BaseInteractorImp(), SegmentBaseInteractor {

    override suspend fun fetchMarkdowns(subjectId: Long) = segmentRepo.loadMarkdowns(subjectId)

    override suspend fun insertMarkdown(markdown: Markdown) = segmentRepo.saveMarkdown(markdown)

    override suspend fun insertChecklist(checklist: Checklist) = segmentRepo.saveChecklist(checklist)

    override suspend fun fetchModule(moduleId: Long) = segmentRepo.loadModule(moduleId)

    override suspend fun fetchSubject(subjectId: Long) = segmentRepo.loadSubject(subjectId)
}