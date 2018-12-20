package org.secfirst.umbrella.whitelabel.data.database.content

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.disk.Root
import javax.inject.Inject

class ContentRepository @Inject constructor(private val contentDao: ContentDao) : ContentRepo {

    override suspend fun resetContent() = contentDao.resetContent()

    override suspend fun insertDefaultRSS(rssList: List<RSS>) = contentDao.insertDefaultRSS(rssList)

    override suspend fun getSubject(sha1ID: String) = contentDao.getSubject(sha1ID)

    override suspend fun getDifficulty(sha1ID: String) = contentDao.getDifficulty(sha1ID)

    override suspend fun getModule(sha1ID: String) = contentDao.getModule(sha1ID)

    override suspend fun getMarkdown(sha1ID: String) = contentDao.getMarkdown(sha1ID)

    override suspend fun getChecklist(sha1ID: String) = contentDao.getChecklist(sha1ID)

    override suspend fun getForm(sha1ID: String) = contentDao.getForm(sha1ID)

    override suspend fun saveAllChecklists(checklists: List<Checklist>) = contentDao.saveChecklists(checklists)

    override suspend fun saveAllMarkdowns(markdowns: List<Markdown>) = contentDao.saveMarkdowns(markdowns)

    override suspend fun saveAllModule(modules: List<Module>) = contentDao.saveModules(modules)

    override suspend fun saveAllDifficulties(difficulties: List<Difficulty>) = contentDao.saveDifficulties(difficulties)

    override suspend fun saveAllForms(forms: List<Form>) = contentDao.saveForms(forms)

    override suspend fun saveAllSubjects(subjects: List<Subject>) = contentDao.saveSubjects(subjects)

    override suspend fun insertFeedSource(feedSources: List<FeedSource>) = contentDao.insertFeedSource(feedSources)

    override suspend fun insertAllLessons(root: Root) = contentDao.insertAllLessons(root)

}