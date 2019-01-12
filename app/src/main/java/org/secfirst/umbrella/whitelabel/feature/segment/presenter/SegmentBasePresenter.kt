package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView


interface SegmentBasePresenter<V : SegmentView, I : SegmentBaseInteractor> : BasePresenter<V, I> {

//    fun submitLoadSegments(difficultyId: String)
//
//    fun submitLoadModule(moduleId: String)
//
//    fun submitLoadSubject(subjectId: String)

    fun submitLoadDifficulties(difficultyIds: ArrayList<String>)

    fun submitChecklistFavorite(checklist: Checklist)

    fun submitMarkdownFavorite(markdown: Markdown)

    fun submitDifficultySelected(subjectSha1ID: String, difficulty: Difficulty)

    fun submitMarkdowns(markdownIds: ArrayList<String>)

    fun submitMarkdownsAndChecklist(markdownIds: ArrayList<String>, checklistId: String)

    fun submitDifficulties(difficultyIds: ArrayList<String>)
}