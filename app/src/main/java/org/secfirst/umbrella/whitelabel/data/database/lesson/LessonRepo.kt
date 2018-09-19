package org.secfirst.umbrella.whitelabel.data.database.lesson

import org.secfirst.umbrella.whitelabel.data.Markdown
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

interface LessonRepo {

    suspend fun loadAllCategories(): List<Category>

    suspend fun loadSubcategoryBy(categoryId: Long): Subcategory

    suspend fun loadChildBy(subcategoryId: Long, difficultTitle: String): Child

    suspend fun loadAllMarkdowns(subcategoryId: Long): List<Markdown>
}