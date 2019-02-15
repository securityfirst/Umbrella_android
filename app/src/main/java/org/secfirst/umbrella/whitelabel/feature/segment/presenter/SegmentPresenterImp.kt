package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView
import org.secfirst.umbrella.whitelabel.misc.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import javax.inject.Inject


class SegmentPresenterImp<V : SegmentView, I : SegmentBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), SegmentBasePresenter<V, I> {

    override fun submitMarkdownsByURI(uri: String) {
        val uriWithoutHost = uri.substringAfterLast("$LESSON_HOST/")
        val uriSplitted = uriWithoutHost.split("/")

        when (uriSplitted.size) {
            LESSON_SUBJECT_LEVEL -> deepLinkForSubject(uriSplitted)
            LESSON_MODULE_LEVEL -> deepLinkForModule(uriSplitted)
            LESSON_SEGMENT_IN_MODULE -> deepLinkForSegmentInModule(uriSplitted)
            LESSON_SEGMENT_IN_SUBJECT -> deepLinkForSegmentInSubject(uriSplitted)
        }
    }

    private fun deepLinkForSegmentInModule(uriSplitted: List<String>) {
        launchSilent(uiContext) {
            interactor?.let {
                var indexSelected = 0
                val moduleSelected = uriSplitted[0]
                var segmentSelected = uriSplitted[1]
                segmentSelected = segmentSelected.replace("_", " ")
                val module = it.fetchModuleByRootDir(moduleSelected)

                if (module?.subjects != null) {
                    module.subjects.forEach { subject ->
                        if (subject.rootDir.capitalize() == segmentSelected.capitalize()) {
                            getView()?.showSegments(subject.markdowns)
                        }
                    }
                } else {
                    module?.markdowns?.let { markdowns ->
                        markdowns.forEach { markdown ->
                            if (markdown.title.capitalize() == segmentSelected.capitalize())
                                indexSelected = markdown.index.toInt()
                        }
                        getView()?.showSegments(markdowns, indexSelected)
                    }
                }
            }
        }
    }

    private fun deepLinkForModule(uriSplitted: List<String>) {
        launchSilent(uiContext) {
            interactor?.let {
                val moduleSelected = uriSplitted[0]
                val module = it.fetchModuleByRootDir(moduleSelected)
                module?.markdowns?.let { markdowns -> getView()?.showSegments(markdowns) }
            }
        }
    }

    private fun deepLinkForSegmentInSubject(uriSplitted: List<String>) {
        launchSilent(uiContext) {
            interactor?.let {
                val subjectTitle = uriSplitted[2]
                val subject = it.fetchSubjectByRootDir(subjectTitle)
                val difficultySelected = uriSplitted[1]
                var segmentSelected = uriSplitted.last()
                segmentSelected = segmentSelected.replace("_", " ")
                var indexSelected = 0

                subject?.let { safeSubject ->
                    val difficulties = it.fetchDifficultyBySubject(safeSubject.id).toMutableList()
                    difficulties.forEach { difficulty ->
                        if (difficulty.rootDir == difficultySelected)
                            difficulty.markdowns.forEach { markdown ->
                                var markdownTitle = markdown.title.capitalize()
                                markdownTitle = markdownTitle.replace("?", "")
                                if (markdownTitle == segmentSelected.capitalize())
                                    indexSelected = markdown.index.toInt()
                            }
                    }
                    getView()?.showSegmentsWithDifficulty(sortDifficulty(difficulties, difficultySelected), indexSelected)
                }
            }
        }
    }

    private fun deepLinkForSubject(uriSplitted: List<String>) {
        launchSilent(uiContext) {
            interactor?.let {
                val subjectTitle = uriSplitted.last()
                val subject = it.fetchSubjectByRootDir(subjectTitle)
                val difficultySelected = uriSplitted[1]

                subject?.let { safeSubject ->
                    val difficulties = it.fetchDifficultyBySubject(safeSubject.id).toMutableList()
                    getView()?.showSegmentsWithDifficulty(sortDifficulty(difficulties, difficultySelected))
                }
            }
        }
    }

    private fun sortDifficulty(difficulties: List<Difficulty>, difficultySelected: String): List<Difficulty> {
        val sortDifficulties = mutableListOf<Difficulty>()
        difficulties.forEach { diff ->
            if (diff.rootDir == difficultySelected)
                sortDifficulties.add(diff)
        }
        difficulties.forEach { diff ->
            if (diff.rootDir != difficultySelected)
                sortDifficulties.add(diff)
        }

        return sortDifficulties
    }

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
                    it.fetchDifficulty(id)?.let { diff ->
                        diff.subject?.let { safeSubject ->
                            val fullSubject = it.fetchSubject(safeSubject.id)
                            diff.subject = fullSubject
                        }
                        difficulties.add(diff)
                    }
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
                    it.fetchMarkdown(markdownId)?.let { markdown ->
                        markdowns.add(markdown)
                    }
                }
                getView()?.showSegments(markdowns)
            }
        }
    }

    override fun submitDifficultySelected(subjectId: String, difficulty: Difficulty) {
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

    override fun submitTitleToolbar(subjectId: String, moduleId: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val title: String
                title = if (subjectId.isNotEmpty()) {
                    val subject = it.fetchSubject(subjectId)
                    subject?.title ?: ""
                } else {
                    val module = it.fetchModule(moduleId)
                    module?.moduleTitle ?: ""
                }
                getView()?.getTitleToolbar(title)
            }
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