package org.secfirst.umbrella.whitelabel.data.database.lesson

import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.content.Subject

interface LessonRepo {

    suspend fun loadAllCategories(): List<Module>

    suspend fun loadSubcategoryBy(id: Long): Subject?

    suspend fun loadCategoryBy(id: Long): Module?

    suspend fun loadChildBy(id: Long): Difficulty?

    suspend fun loadTopicPreferredBy(subcategoryId: Long): TopicPreferred?

}