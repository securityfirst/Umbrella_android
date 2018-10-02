package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import android.util.Log
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.data.database.lesson.toLesson
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown.Companion.SINGLE_CHOICE
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class LessonPresenterImp<V : LessonView, I : LessonBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LessonBasePresenter<V, I> {


    override fun submitSelectHead(moduleId: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val module = it.fetchCategoryBy(moduleId)
                module?.let { safeModule ->
                    Log.e("test", "$module")
                    if (safeModule.markdowns.size > SINGLE_CHOICE) {
                        getView()?.startSegmentController(safeModule)
                    } else {
                        if (safeModule.markdowns.size == SINGLE_CHOICE) {
                            val singleMarkdown = safeModule.markdowns.last()
                            getView()?.startSegmentDetail(singleMarkdown)
                        } else {

                        }
                    }
                }
            }
        }
    }

    override fun submitSelectLesson(subject: Subject) {
        launchSilent(uiContext) {
            interactor?.let {
                var topicPreferred: TopicPreferred? = null
                subject.difficulties.forEach { difficulty ->
                    val candidateTopic = it.fetchTopicPreferredBy(difficulty.id)
                    if (candidateTopic != null)
                        topicPreferred = candidateTopic
                }
                val markdown = it.fetchMarkdownBySubject(subject.id)

                if (topicPreferred != null)
                    subjectInSegment(topicPreferred?.difficulty)
                else if (subject.difficulties.isEmpty() && markdown != null) {
                    subjectInSegmentDetail(markdown)
                } else {
                    subjectInDifficulty(subject)
                }
            }
        }
    }

    private fun subjectInDifficulty(subject: Subject) {
        getView()?.startDifficultyController(subject)
    }

    private fun subjectInSegment(selectDifficulty: Difficulty?) {
        selectDifficulty?.let { getView()?.startDeferredSegment(it) }
    }

    private fun subjectInSegmentDetail(markdown: Markdown) {
        getView()?.startSegmentDetail(markdown)
    }

    override fun submitLoadAllLesson() {
        launchSilent(uiContext) {
            interactor?.let {
                val categories = it.fetchCategories()
                        .asSequence()
                        .filter { category -> category.title != "" }
                        .toList()
                getView()?.showAllLesson(categories.toLesson())
            }
        }
    }
}