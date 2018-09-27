package org.secfirst.umbrella.whitelabel.feature.difficulty.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface DifficultyBaseInteractor : BaseInteractor {

    suspend fun insertTopicPreferred(topicPreferred: TopicPreferred)

    suspend fun fetchSubcategoryBy(subcategoryId: Long): Subcategory?

    suspend fun fetchChildBy(id: Long): Child?

}