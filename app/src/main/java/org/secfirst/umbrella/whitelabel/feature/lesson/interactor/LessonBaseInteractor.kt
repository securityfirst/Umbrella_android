package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.Markdown
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchCategories(): List<Category>

    suspend fun fetchSubcategoryBy(categoryId: Long): Subcategory

    suspend fun fetchChildBy(subcategory: Long, difficultTitle: String): Child

    suspend fun fetchAllMarkdownsBy(subcategory: Long): List<Markdown>
}