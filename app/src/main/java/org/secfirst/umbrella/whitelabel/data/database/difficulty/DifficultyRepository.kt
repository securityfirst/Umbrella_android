package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import javax.inject.Inject

class DifficultyRepository @Inject constructor(private val diffDao: DifficultyDao) : DifficultyRepo {

    override suspend fun loadChildBy(id: Long) = diffDao.getChildBy(id)

    override suspend fun loadSubcategoryBy(subcategoryId: Long) = diffDao.getSubcategoryBy(subcategoryId)

    override suspend fun saveTopicPreferred(topicPreferred: TopicPreferred) = diffDao.save(topicPreferred)
}