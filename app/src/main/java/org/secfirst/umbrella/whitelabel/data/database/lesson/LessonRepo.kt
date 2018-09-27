package org.secfirst.umbrella.whitelabel.data.database.lesson

import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

interface LessonRepo {

    suspend fun loadAllCategories(): List<Category>

    suspend fun loadSubcategoryBy(id: Long): Subcategory?

    suspend fun loadCategoryBy(id: Long): Category?

    suspend fun loadChildBy(id: Long): Child?

    suspend fun loadTopicPreferredBy(subcategoryId: Long): TopicPreferred?

}