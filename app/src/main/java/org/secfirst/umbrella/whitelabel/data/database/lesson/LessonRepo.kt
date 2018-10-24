package org.secfirst.umbrella.whitelabel.data.database.lesson

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.TopicPreferred
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown

interface LessonRepo {

    suspend fun loadAllCategories(): List<Module>

    suspend fun loadSubcategoryBy(id: Long): Subject?

    suspend fun loadCategoryBy(id: Long): Module?

    suspend fun loadMarkdownBySubject(subjectId: Long): List<Markdown>

    suspend fun loadMarkdownByModule(moduleId: Long): Markdown?

    suspend fun loadMarkdownsBy(id: Long): List<Markdown>

    suspend fun loadChildBy(id: Long): Difficulty?

    suspend fun loadTopicPreferredBy(subjectId: Long): TopicPreferred?

}