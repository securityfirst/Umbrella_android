package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import javax.inject.Inject

class DifficultyRepository @Inject constructor(private val diffDao: DifficultyDao) : DifficultyRepo {

    override suspend fun loadSubcategoryBy(subcategory: Long) = diffDao.getSubcategoryBy(subcategory)

    override suspend fun saveTopicPreferred(topicPreferred: TopicPreferred) = diffDao.save(topicPreferred)

}