package org.secfirst.umbrella.whitelabel.data.database.lesson

import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) : LessonRepo {

    override suspend fun loadAllFavoriteSubjects() = lessonDao.getFavoriteMarkdown()

    override suspend fun loadMarkdownByModule(moduleId: Long) = lessonDao.getMarkdownByModule(moduleId)

    override suspend fun loadMarkdownsBy(sha1ID : String) = lessonDao.getMarkdowns(sha1ID)

    override suspend fun loadMarkdownBySubject(id: Long) = lessonDao.getMarkdownBySubject(id)

    override suspend fun loadDifficultyBy(id: Long) = lessonDao.getDifficultyBy(id)

    override suspend fun loadLessonBy(id: Long) = lessonDao.getLessonBy(id)

    override suspend fun loadDifficultyPreferredBy(subjectId: Long) = lessonDao.getDifficultyPreferred(subjectId)

    override suspend fun loadSubject(id: Long) = lessonDao.getSubject(id)

    override suspend fun loadAllModules() = lessonDao.getAllLesson()
}