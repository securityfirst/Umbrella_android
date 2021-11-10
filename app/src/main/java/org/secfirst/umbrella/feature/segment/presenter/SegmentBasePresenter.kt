package org.secfirst.umbrella.feature.segment.presenter

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.feature.segment.view.SegmentView


interface SegmentBasePresenter<V : SegmentView, I : SegmentBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadDifficulties(difficultyIds: ArrayList<String>)

    fun submitChecklistFavorite(checklist: Checklist)

    fun submitMarkdownFavorite(markdown: Markdown)

    fun submitDifficultySelected(subjectId: String, difficulty: Difficulty)

    fun submitMarkdowns(markdownIds: ArrayList<String>)

    fun submitMarkdownsAndChecklist(markdownIds: ArrayList<String>, checklistId: String)

    fun submitDifficulties(difficultyIds: ArrayList<String>)

    fun submitTitleToolbar(subjectId: String = "", moduleId: String = "")

    fun submitMarkdownsByURI(uri: String)
}