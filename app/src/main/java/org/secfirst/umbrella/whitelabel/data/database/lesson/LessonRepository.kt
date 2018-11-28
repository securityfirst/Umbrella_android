package org.secfirst.umbrella.whitelabel.data.database.lesson

import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) : LessonRepo {

    override suspend fun loadAllFavoriteSubjects() = lessonDao.getFavoriteMarkdown()

    override suspend fun loadMarkdownByModule(moduleSh1ID: String) = lessonDao.getMarkdownByModule(moduleSh1ID)

    override suspend fun loadMarkdownsBy(sha1ID : String) = lessonDao.getMarkdowns(sha1ID)

    override suspend fun loadMarkdownBySubject(sha1ID: String) = lessonDao.getMarkdownBySubject(sha1ID)

    override suspend fun loadDifficultyBy(id: Long) = lessonDao.getDifficultyBy(id)

    override suspend fun loadLessonBy(sha1ID: String) = lessonDao.getLessonBy(sha1ID)

    override suspend fun loadDifficultyPreferredBy(subjectSha1ID : String) = lessonDao.getDifficultyPreferred(subjectSha1ID)

    override suspend fun loadSubject(sha1ID: String) = lessonDao.getSubject(sha1ID)

    override suspend fun loadAllModules() = lessonDao.getAllLesson()
}