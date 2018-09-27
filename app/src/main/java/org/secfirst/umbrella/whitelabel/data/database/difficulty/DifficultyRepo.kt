package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred

interface DifficultyRepo {

    suspend fun loadChildBy(id: Long): Child?

    suspend fun loadSubcategoryBy(subcategoryId: Long): Subcategory?

    suspend fun saveTopicPreferred(topicPreferred: TopicPreferred)
}