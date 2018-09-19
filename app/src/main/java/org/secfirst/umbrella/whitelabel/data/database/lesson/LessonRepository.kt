package org.secfirst.umbrella.whitelabel.data.database.lesson

import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) : LessonRepo {

    override suspend fun loadCategoryBy(id: Long) = lessonDao.getCategoryBy(id)

    override suspend fun loadAllCategories() = lessonDao.getAllLesson()
}