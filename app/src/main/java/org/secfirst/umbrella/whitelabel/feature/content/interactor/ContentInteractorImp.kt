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
import org.secfirst.umbrella.whitelabel.data.disk.Root
import org.secfirst.umbrella.whitelabel.data.disk.TentRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import org.secfirst.umbrella.whitelabel.serialize.ElementLoader
import org.secfirst.umbrella.whitelabel.serialize.ElementSerialize
import javax.inject.Inject

class ContentInteractorImp @Inject constructor(apiHelper: ApiHelper,
                                               preferenceHelper: AppPreferenceHelper,
                                               contentRepo: ContentRepo,
                                               private val tentRepo: TentRepo,
                                               private val elementSerialize: ElementSerialize,
                                               private val elementLoader: ElementLoader)
    : BaseInteractorImp(apiHelper, preferenceHelper, contentRepo), ContentBaseInteractor {

    override suspend fun resetDatabase() = contentRepo.resetContent()

    override suspend fun persistRSS(rssList: List<RSS>) = contentRepo.insertDefaultRSS(rssList)

    override suspend fun getSubject(sha1ID: String) = contentRepo.getSubject(sha1ID)

    override suspend fun getDifficulty(sha1ID: String) = contentRepo.getDifficulty(sha1ID)

    override suspend fun getModule(sha1ID: String) = contentRepo.getModule(sha1ID)

    override suspend fun getMarkdown(sha1ID: String) = contentRepo.getMarkdown(sha1ID)

    override suspend fun getChecklist(sha1ID: String) = contentRepo.getChecklist(sha1ID)

    override suspend fun getForm(sha1ID: String) = contentRepo.getForm(sha1ID)

    override suspend fun saveAllChecklists(checklists: List<Checklist>) = contentRepo.saveAllChecklists(checklists)

    override suspend fun saveAllMarkdowns(markdowns: List<Markdown>) = contentRepo.saveAllMarkdowns(markdowns)

    override suspend fun saveAllModule(modules: List<Module>) = contentRepo.saveAllModule(modules)

    override suspend fun saveAllDifficulties(difficulties: List<Difficulty>) = contentRepo.saveAllDifficulties(difficulties)

    override suspend fun saveAllForms(forms: List<Form>) = contentRepo.saveAllForms(forms)

    override suspend fun saveAllSubjects(subjects: List<Subject>) = contentRepo.saveAllSubjects(subjects)

    override suspend fun persistFeedSource(feedSources: List<FeedSource>) = contentRepo.insertFeedSource(feedSources)

    override suspend fun fetchData() = tentRepo.fetchRepository()

    override suspend fun initParser() = elementLoader.load(elementSerialize.process())

    override suspend fun persist(root: Root) = contentRepo.insertAllLessons(root)

}