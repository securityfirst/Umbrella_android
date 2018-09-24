package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred

interface DifficultyRepo {

    suspend fun loadSubcategoryBy(categoryId: Long): Subcategory

    suspend fun saveTopicPreferred(topicPreferred: TopicPreferred)

}