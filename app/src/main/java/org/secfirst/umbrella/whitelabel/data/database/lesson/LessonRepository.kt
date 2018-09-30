package org.secfirst.umbrella.whitelabel.data.database.lesson

import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) : LessonRepo {

    override suspend fun loadMarkdownByModule(moduleId: Long) = lessonDao.getMarkdownByModule(moduleId)

    override suspend fun loadMarkdownsBy(id: Long) = lessonDao.getMarkdowns(id)

    override suspend fun loadMarkdownBySubject(id: Long) = lessonDao.getMarkdownBySubject(id)

    override suspend fun loadChildBy(id: Long) = lessonDao.getChildBy(id)

    override suspend fun loadCategoryBy(id: Long) = lessonDao.getCategoryBy(id)

    override suspend fun loadTopicPreferredBy(difficultyId: Long) = lessonDao.getTopicPreferred(difficultyId)

    override suspend fun loadSubcategoryBy(id: Long) = lessonDao.getSubcategoryBy(id)

    override suspend fun loadAllCategories() = lessonDao.getAllCategory()
}