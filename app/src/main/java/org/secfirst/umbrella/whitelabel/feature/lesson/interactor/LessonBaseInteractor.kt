package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchCategories(): List<Category>

    suspend fun fetchCategoryBy(id: Long): Category?

    suspend fun fetchSubcategoryBy(id: Long): Subcategory?

    suspend fun fetchChildBy(id: Long): Child?

    suspend fun fetchTopicPreferredBy(subcategory: Long): TopicPreferred?
}