package org.secfirst.umbrella.whitelabel.data.database.lesson

import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) : LessonRepo {

    override suspend fun loadDifficultyBySubject(subjectId: String) = lessonDao.getDifficultyBySubject(subjectId)

    override suspend fun loadAllFavoriteSubjects() = lessonDao.getFavoriteMarkdown()

    override suspend fun loadMarkdownBySubject(subjectId: String) = lessonDao.getMarkdownBySubject(subjectId)

    override suspend fun loadLessonBy(sha1ID: String) = lessonDao.getLessonBy(sha1ID)

    override suspend fun loadDifficultyPreferredBy(subjectId: String) = lessonDao.getDifficultyPreferred(subjectId)

    override suspend fun loadAllModules() = lessonDao.getAllLesson()
}