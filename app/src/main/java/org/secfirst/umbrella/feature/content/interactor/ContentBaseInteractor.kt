package org.secfirst.umbrella.feature.content.interactor

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.content.ContentData
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.database.reader.RSS
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.interactor.BaseInteractor

interface ContentBaseInteractor : BaseInteractor {

    suspend fun fetchData(url: String): Boolean

    suspend fun persist(contentData: ContentData)

    suspend fun persistFeedSource(feedSources: List<FeedSource>)

    suspend fun persistRSS(rssList: List<RSS>)

    suspend fun saveAllChecklists(checklists: List<Checklist>)

    suspend fun saveAllMarkdowns(markdowns: List<Markdown>)

    suspend fun saveAllModule(modules: List<Module>)

    suspend fun saveAllDifficulties(difficulties: List<Difficulty>)

    suspend fun saveAllForms(forms: List<Form>)

    suspend fun saveAllSubjects(subjects: List<Subject>)

    suspend fun getSubject(subjectId: String): Subject?

    suspend fun getDifficulty(difficultyId: String): Difficulty?

    suspend fun getModule(moduleId: String): Module?

    suspend fun getMarkdown(markdownId: String): Markdown?

    suspend fun getChecklist(checklistId: String): Checklist?

    suspend fun getForm(formId: String): Form?

    suspend fun resetDatabase(): Boolean
}