package org.secfirst.umbrella.whitelabel.data.database.content

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import javax.inject.Inject

class ContentRepository @Inject constructor(private val contentDao: ContentDao) : ContentRepo {

    override suspend fun resetContent() = contentDao.resetContent()

    override suspend fun insertDefaultRSS(rssList: List<RSS>) = contentDao.insertDefaultRSS(rssList)

    override suspend fun getSubject(subjectId: String) = contentDao.getSubject(subjectId)

    override suspend fun getDifficulty(difficultyId: String) = contentDao.getDifficulty(difficultyId)

    override suspend fun getModule(moduleId: String) = contentDao.getModule(moduleId)

    override suspend fun getMarkdown(markdownId: String) = contentDao.getMarkdown(markdownId)

    override suspend fun getChecklist(checklistId: String) = contentDao.getChecklist(checklistId)

    override suspend fun getForm(formId: String) = contentDao.getForm(formId)

    override suspend fun saveAllChecklists(checklists: List<Checklist>) = contentDao.saveChecklists(checklists)

    override suspend fun saveAllMarkdowns(markdowns: List<Markdown>) = contentDao.saveMarkdowns(markdowns)

    override suspend fun saveAllModule(modules: List<Module>) = contentDao.saveModules(modules)

    override suspend fun saveAllDifficulties(difficulties: List<Difficulty>) = contentDao.saveDifficulties(difficulties)

    override suspend fun saveAllForms(forms: List<Form>) = contentDao.saveForms(forms)

    override suspend fun saveAllSubjects(subjects: List<Subject>) = contentDao.saveSubjects(subjects)

    override suspend fun insertFeedSource(feedSources: List<FeedSource>) = contentDao.insertFeedSource(feedSources)

    override suspend fun insertAllLessons(contentData: ContentData) = contentDao.insertAllContent(contentData)

}