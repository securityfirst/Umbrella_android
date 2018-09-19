package org.secfirst.umbrella.whitelabel.data.database.lesson

import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

interface LessonRepo {

    suspend fun loadAllCategories(): List<Category>

    suspend fun loadCategoryBy(id: Long): Subcategory
}