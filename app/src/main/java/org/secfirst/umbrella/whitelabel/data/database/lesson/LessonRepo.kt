package org.secfirst.umbrella.whitelabel.data.database.lesson

import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.disk.Markdown

interface LessonRepo {

    suspend fun saveTopicPreffered(topicPreferred: TopicPreferred)

    suspend fun loadAllCategories(): List<Category>

    suspend fun loadSubcategoryBy(categoryId: Long): Subcategory

    suspend fun loadTopicPreferredBy(subcategoryId: Long): TopicPreferred?

    suspend fun loadChildBy(subcategoryId: Long): Child?

    suspend fun loadAllMarkdowns(subcategoryId: Long): List<Markdown>
}