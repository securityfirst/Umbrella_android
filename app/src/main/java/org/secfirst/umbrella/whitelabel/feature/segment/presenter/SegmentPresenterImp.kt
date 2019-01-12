package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject


class SegmentPresenterImp<V : SegmentView, I : SegmentBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), SegmentBasePresenter<V, I> {


    override fun submitMarkdownsAndChecklist(markdownIds: ArrayList<String>, checklistId: String) {
        launchSilent(uiContext) {
            val markdowns = mutableListOf<Markdown>()
            interactor?.let {
                markdownIds.forEach { markdownId ->
                    it.fetchMarkdown(markdownId)?.let { markdown -> markdowns.add(markdown) }
                }
                val checklist = it.fetchChecklist(checklistId)
                getView()?.showSegments(markdowns, checklist)
            }
        }
    }

    override fun submitDifficulties(difficultyIds: ArrayList<String>) {
        launchSilent(uiContext) {
            val difficulties = mutableListOf<Difficulty>()
            interactor?.let {
                difficultyIds.forEach { id ->
                    it.fetchDifficulty(id)?.let { diff -> difficulties.add(diff) }
                }
                getView()?.showSegmentsWithDifficulty(difficulties)
            }
        }
    }

    override fun submitMarkdowns(markdownIds: ArrayList<String>) {
        launchSilent(uiContext) {
            val markdowns = mutableListOf<Markdown>()
            interactor?.let {
                markdownIds.forEach { markdownId ->
                    it.fetchMarkdown(markdownId)?.let { markdown -> markdowns.add(markdown) }
                }
                getView()?.showSegments(markdowns)
            }
        }
    }

    override fun submitDifficultySelected(subjectSha1ID: String, difficulty: Difficulty) {
        launchSilent(uiContext) {
            interactor?.insertDifficultySelect(subjectSha1ID, difficulty)
        }
    }

    override fun submitMarkdownFavorite(markdown: Markdown) {
        launchSilent(uiContext) {
            interactor?.insertMarkdown(markdown)
        }
    }

    override fun submitChecklistFavorite(checklist: Checklist) {
        launchSilent(uiContext) {
            interactor?.insertChecklist(checklist)
        }
    }

    override fun submitLoadDifficulties(difficultyIds: ArrayList<String>) {
        launchSilent(uiContext) {
            interactor?.let {
                val difficulties = mutableListOf<Difficulty>()
                difficultyIds.forEach { id ->
                    it.fetchDifficulty(id)?.let { difficulty -> difficulties.add(difficulty) }
                }
                getView()?.showSegmentsWithDifficulty(difficulties)
            }
        }
    }
}