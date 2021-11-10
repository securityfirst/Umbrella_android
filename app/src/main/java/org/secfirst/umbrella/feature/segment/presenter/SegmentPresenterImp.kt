package org.secfirst.umbrella.feature.segment.presenter

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.feature.segment.view.SegmentView
import org.secfirst.umbrella.misc.*
import org.secfirst.umbrella.misc.*
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import javax.inject.Inject


class SegmentPresenterImp<V : SegmentView, I : SegmentBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), SegmentBasePresenter<V, I> {

    override fun submitMarkdownsByURI(uri: String) {
        val uriWithoutHost = uri.substringAfterLast(SCHEMA)
        val uriSplitted = uriWithoutHost.split("/")
        val fileExtension = uriSplitted.last()
        if (fileExtension.contains("s_", true)) {
            when (uriSplitted.size) {
                4 -> findSegmentInSubject(uriSplitted)
                3 -> findSegmentInModule(uriSplitted)
                2 -> findSegmentInModule(uriSplitted)
            }
        } else {
            findSegmentInSubject(uriSplitted)
        }
    }

    private fun findSegmentInModule(uriSplitted: List<String>) {
        launchSilent(uiContext) {
            interactor?.let {
                var indexSelected = 0
                val moduleSelected = uriSplitted[0]
                val segmentSelected = uriSplitted[1]
                val module = it.fetchModuleByRootDir(moduleSelected.trim())

                module?.let { safeModule ->
                    if (safeModule.subjects.isNotEmpty()) {
                        safeModule.subjects.forEach { subject ->
                            if (subject.rootDir.removeSpecialCharacter().toLowerCase() == segmentSelected.deepLinkIdentifier()) {
                                getView()?.showSegments(subject.markdowns)
                            }
                        }
                    } else {
                        safeModule.markdowns.let { markdowns ->
                            markdowns.forEach { markdown ->
                                val segment = markdown.id.split("/").last().deepLinkIdentifier().toLowerCase()
                                if (segment == segmentSelected.deepLinkIdentifier())
                                    indexSelected = markdown.index.toInt()
                            }
                            getView()?.showSegments(markdowns, indexSelected)
                        }
                    }

                }
            }
        }
    }

    private fun findSegmentInSubject(uriSplitted: List<String>) {
        launchSilent(uiContext) {
            interactor?.let {
                val subjectTitle = uriSplitted[1]
                val subject = it.fetchSubjectByRootDir(subjectTitle)
                val difficultySelected = uriSplitted[2]
                val segmentSelected = uriSplitted.last()
                var indexSelected = 0

                subject?.let { safeSubject ->
                    val difficulties = it.fetchDifficultyBySubject(safeSubject.id).toMutableList()
                    difficulties.forEach { difficulty ->
                        if (difficulty.rootDir == difficultySelected)
                            difficulty.markdowns.forEach { markdown ->
                                val segment = markdown.id.split("/").last().deepLinkIdentifier().toLowerCase()
                                if (segment == segmentSelected.deepLinkIdentifier())
                                    indexSelected = markdown.index.toInt()
                                if (segmentSelected == CHECKLIST_EXTENSION)
                                    indexSelected = difficulty.markdowns.size + 1
                            }
                    }
                    getView()?.showSegmentsWithDifficulty(sortDifficulty(difficulties, difficultySelected), indexSelected)
                }
            }
        }
    }

    override fun submitMarkdownsAndChecklist(markdownIds: ArrayList<String>, checklistId: String) {
        launchSilent(uiContext) {
            val markdowns = mutableListOf<Markdown>()
            var index = 0
            interactor?.let {
                markdownIds.forEach { markdownId ->
                    it.fetchMarkdown(markdownId)?.let { markdown -> markdowns.add(markdown) }
                }
                val checklist = it.fetchChecklist(checklistId)
                markdowns.sortBy { it.title.capitalize() }

                // In case of uneven number of markdowns, add an empty markdown so that the last markdown doesn't take the entire width
                if (markdowns.size % 2 != 0) {
                    val emptyBufferMark = Markdown()
                    emptyBufferMark.isRemove = true
                    emptyBufferMark.index = "500"
                    markdowns.add(markdowns.size, emptyBufferMark)
                }

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
                markdowns.sortBy { it.title.capitalize() }
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
                    module?.title ?: ""
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
}