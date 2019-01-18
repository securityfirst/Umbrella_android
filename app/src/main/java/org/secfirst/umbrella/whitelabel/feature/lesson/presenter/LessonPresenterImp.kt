package org.secfirst.umbrella.whitelabel.feature.lesson.presenter


import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.ids
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.toLesson
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown.Companion.SINGLE_CHOICE
import org.secfirst.umbrella.whitelabel.data.database.segment.ids
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class LessonPresenterImp<V : LessonView, I : LessonBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LessonBasePresenter<V, I> {

    override fun submitSelectHead(moduleId: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val module = it.fetchLesson(moduleId)
                val favorites = it.fetchAllFavorites()
                module?.let { safeModule ->
                    when {
                        safeModule.markdowns.size == SINGLE_CHOICE -> getView()?.startSegmentAlone(safeModule.markdowns.last())
                        safeModule.markdowns.size > SINGLE_CHOICE -> getView()?.startSegment(safeModule.markdowns.ids(), false)
                        moduleId == Module.FAVORITE_ID -> getView()?.startSegment(favorites.ids(), false)
                        else -> ""
                    }
                }
            }
        }
    }

    override fun submitSelectLesson(subject: Subject) {
        launchSilent(uiContext) {
            interactor?.let {
                val difficulties = it.fetchDifficultyBySubject(subject.id)
                val difficultyPreferred = it.fetchDifficultyPreferredBy(subject.id)
                val markdowns = it.fetchMarkdownBySubject(subject.id)
                val sortDifficulties = mutableListOf<Difficulty>()

                when {
                    difficultyPreferred != null -> {
                        difficultyPreferred.difficulty?.let { safePreferred ->
                            sortDifficulties.add(safePreferred)
                            difficulties.forEach { diff -> if (diff.id != safePreferred.id) sortDifficulties.add(diff) }
                            getView()?.startSegment(sortDifficulties.ids(), true)
                        }

                    }
                    markdowns.isNotEmpty() -> getView()?.startSegment(markdowns.ids(), false)

                    else -> getView()?.startDifficultyController(subject)
                }
            }
        }
    }

    override fun submitLoadAllLesson() {
        launchSilent(uiContext) {
            interactor?.let {
                val markdownsFavorite = it.fetchAllFavorites()
                val modules = it.fetchModules()
                        .filter { lesson -> lesson.moduleTitle != "" }
                        .toList()
                modules[0].markdowns = markdownsFavorite.toMutableList()
                getView()?.showAllLesson(modules.toLesson())
            }
        }
    }
}