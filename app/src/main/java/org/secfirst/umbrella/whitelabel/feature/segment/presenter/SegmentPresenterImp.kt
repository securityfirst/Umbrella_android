package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.defaultDifficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.orderDifficulty
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


    override fun submitDataSegments(difficultyId: String, checklistId: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val markdowns = it.fetchMarkdownsFromDifficulty(difficultyId)
                val checklist = it.fetchChecklist(checklistId)
                getView()?.showSegments(markdowns, checklist)
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

    override fun submitLoadSubject(subjectId: String) {
        launchSilent(uiContext) {
            val subject = interactor?.fetchSubject(subjectId)
            val markdowns = interactor?.fetchMarkdowns(subjectId)
            subject?.let { safeSubject ->
                markdowns?.let { safeMarkdown ->
                    if (safeMarkdown.size > Markdown.SINGLE_CHOICE) {
                        val list = mutableListOf<Difficulty>()
                        list.add(defaultDifficulty(safeMarkdown, safeSubject.title))
                        getView()?.showSegments(list)
                    }
                }
            }
        }
    }

    override fun submitLoadModule(moduleId: String) {
        launchSilent(uiContext) {
            val moduleSelected = interactor?.fetchModule(moduleId)
            val markdown = interactor?.fetchMarkdownsFromModule(moduleId)
            moduleSelected?.let {
                markdown?.let { safeMarkdown ->
                    val list = mutableListOf<Difficulty>()
                    list.add(defaultDifficulty(safeMarkdown, it.moduleTitle))
                    getView()?.showSegments(list)
                }
            }
        }
    }

    override fun submitLoadSegments(difficultyId: String) {
        launchSilent(uiContext) {
            interactor?.let { safeInteractor ->
                val difficulty = safeInteractor.fetchDifficulty(difficultyId)
                val subject = safeInteractor.fetchSubject(difficulty?.subject?.id ?: "")
                val orderDifficulties = subject?.difficulties?.orderDifficulty(difficulty!!)
                orderDifficulties?.let {
                    it.forEach { difficulty ->
                        difficulty.subject = safeInteractor.fetchSubject(subject.id)
                    }
                    getView()?.showSegments(it)
                }
            }
        }
    }
}