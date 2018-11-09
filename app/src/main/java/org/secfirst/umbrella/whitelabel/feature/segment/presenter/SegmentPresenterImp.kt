package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.defaultDifficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.orderDifficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
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


    override fun submitDifficultySelected(subjectId: Long, difficulty: Difficulty) {
        launchSilent(uiContext) {
            interactor?.insertDifficultySelect(subjectId, difficulty)
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

    override fun submitLoadSubject(subject: Subject) {
        launchSilent(uiContext) {
            interactor?.let { safeInteractor ->
                val markdowns = safeInteractor.fetchMarkdowns(subject.id)
                if (markdowns.size > Markdown.SINGLE_CHOICE) {
                    val list = mutableListOf<Difficulty>()
                    list.add(defaultDifficulty(markdowns, subject.title))
                    getView()?.showSegments(list)
                }
            }
        }
    }

    override fun submitLoadModule(selectModule: Module) {
        with(selectModule) {
            if (markdowns.size > Markdown.SINGLE_CHOICE) {
                val list = mutableListOf<Difficulty>()
                list.add(defaultDifficulty(markdowns, moduleTitle))
                getView()?.showSegments(list)
            }
        }
    }

    override fun submitLoadSegments(selectDifficulty: Difficulty) {
        launchSilent(uiContext) {
            interactor?.let { safeInteractor ->
                val subject = safeInteractor.fetchSubject(selectDifficulty.subject!!.id)
                val orderDifficulties = subject?.difficulties?.orderDifficulty(selectDifficulty)
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