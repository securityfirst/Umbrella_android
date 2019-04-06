package org.secfirst.umbrella.whitelabel.feature.content.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepo
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.content.ContentData
import org.secfirst.umbrella.whitelabel.data.disk.TentRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import org.secfirst.umbrella.whitelabel.data.disk.TentLoader
import javax.inject.Inject

class ContentInteractorImp @Inject constructor(apiHelper: ApiHelper,
                                               preferenceHelper: AppPreferenceHelper,
                                               contentRepo: ContentRepo,
                                               private val tentRepo: TentRepo,
                                               private val tentLoader: TentLoader)
    : BaseInteractorImp(apiHelper, preferenceHelper, contentRepo), ContentBaseInteractor {

    override suspend fun resetDatabase() = contentRepo.resetContent()

    override suspend fun persistRSS(rssList: List<RSS>) = contentRepo.insertDefaultRSS(rssList)

    override suspend fun getSubject(subjectId: String) = contentRepo.getSubject(subjectId)

    override suspend fun getDifficulty(difficultyId: String) = contentRepo.getDifficulty(difficultyId)

    override suspend fun getModule(moduleId: String) = contentRepo.getModule(moduleId)

    override suspend fun getMarkdown(markdownId: String) = contentRepo.getMarkdown(markdownId)

    override suspend fun getChecklist(checklistId: String) = contentRepo.getChecklist(checklistId)

    override suspend fun getForm(formId: String) = contentRepo.getForm(formId)

    override suspend fun saveAllChecklists(checklists: List<Checklist>) = contentRepo.saveAllChecklists(checklists)

    override suspend fun saveAllMarkdowns(markdowns: List<Markdown>) = contentRepo.saveAllMarkdowns(markdowns)

    override suspend fun saveAllModule(modules: List<Module>) = contentRepo.saveAllModule(modules)

    override suspend fun saveAllDifficulties(difficulties: List<Difficulty>) = contentRepo.saveAllDifficulties(difficulties)

    override suspend fun saveAllForms(forms: List<Form>) = contentRepo.saveAllForms(forms)

    override suspend fun saveAllSubjects(subjects: List<Subject>) = contentRepo.saveAllSubjects(subjects)

    override suspend fun persistFeedSource(feedSources: List<FeedSource>) = contentRepo.insertFeedSource(feedSources)

    override suspend fun fetchData(url: String) = tentRepo.fetchRepository(url)

    override suspend fun initParser() = tentLoader.serializeContent()

    override suspend fun persist(contentData: ContentData) = contentRepo.insertAllLessons(contentData)

}