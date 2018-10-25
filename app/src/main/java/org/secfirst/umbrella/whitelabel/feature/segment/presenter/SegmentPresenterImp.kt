package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.orderDifficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegment
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
        val segments = mutableListOf<Segment>()
        launchSilent(uiContext) {
            interactor?.let {
                val markdowns = it.fetchMarkdowns(subject.id)
                if (markdowns.size > Markdown.SINGLE_CHOICE) {
                    val defaultDifficulty = Difficulty()
                    defaultDifficulty.markdowns.addAll(markdowns)
                    defaultDifficulty.subject = subject
                    val base = mutableListOf<Difficulty>()
                    base.add(defaultDifficulty)
                    getView()?.showSegments(base)
                }
            }
        }
    }

    override fun submitLoadModule(selectModule: Module) {
        val segments = mutableListOf<Segment>()
        with(selectModule) {
            if (markdowns.size > Markdown.SINGLE_CHOICE) {
                //val segment = markdowns.toSegment(title, title, checklist)
                //segments.add(segment)
                val defaultDifficulty = Difficulty()
                defaultDifficulty.title = ""
                defaultDifficulty.markdowns.addAll(markdowns)
                val defaultSubject = Subject()
                defaultSubject.title = title
                defaultDifficulty.subject = defaultSubject
                val base = mutableListOf<Difficulty>()
                base.add(defaultDifficulty)
                getView()?.showSegments(base)
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