package org.secfirst.umbrella.whitelabel.data.database.lesson

import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) : LessonRepo {

    override suspend fun loadTopicPreferredBy(subcategoryId: Long) = lessonDao.getTopic(subcategoryId)

    override suspend fun loadSubcategoryBy(categoryId: Long) = lessonDao.getSubcategoryBy(categoryId)

    override suspend fun loadAllCategories() = lessonDao.getAllCategory()
}