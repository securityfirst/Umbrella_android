package org.secfirst.umbrella.whitelabel.feature.content.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor
import org.yaml.snakeyaml.error.Mark

interface ContentBaseInteractor : BaseInteractor {

    suspend fun fetchData(): Boolean

    suspend fun persist(root: Root)

    suspend fun initParser(): Root

    suspend fun persistFeedSource(feedSources: List<FeedSource>)

    suspend fun persistRSS(rssList: List<RSS>)

    suspend fun saveAllChecklists(checklists: List<Checklist>)

    suspend fun saveAllMarkdowns(markdowns: List<Markdown>)

    suspend fun saveAllModule(modules: List<Module>)

    suspend fun saveAllDifficulties(difficulties: List<Difficulty>)

    suspend fun saveAllForms(forms: List<Form>)

    suspend fun saveAllSubjects(subjects: List<Subject>)

    suspend fun getSubject(sha1ID : String) : Subject?

    suspend fun getDifficulty(sha1ID : String) : Difficulty?

    suspend fun getModule(sha1ID : String) : Module?

    suspend fun getMarkdown(sha1ID : String) : Markdown?

    suspend fun getChecklist(sha1ID : String) : Checklist?

    suspend fun getForm(sha1ID : String) : Form?

    suspend fun resetDatabase()
}