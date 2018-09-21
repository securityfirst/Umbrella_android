package org.secfirst.umbrella.whitelabel.data.database.lesson

import javax.inject.Inject

class LessonRepository @Inject constructor(private val lessonDao: LessonDao) : LessonRepo {

    override suspend fun saveTopicPreffered(topicPreferred: TopicPreferred) = lessonDao.save(topicPreferred)

    override suspend fun loadTopicPreferredBy(subcategoryId: Long) = lessonDao.getTopic(subcategoryId)

    override suspend fun loadChildBy(subcategoryId: Long) = lessonDao.getChildBy(subcategoryId)

    override suspend fun loadAllMarkdowns(subcategoryId: Long) = lessonDao.getAllMarkdownsBy(subcategoryId)

    override suspend fun loadSubcategoryBy(categoryId: Long) = lessonDao.getSubcategoryBy(categoryId)

    override suspend fun loadAllCategories() = lessonDao.getAllCategory()
}