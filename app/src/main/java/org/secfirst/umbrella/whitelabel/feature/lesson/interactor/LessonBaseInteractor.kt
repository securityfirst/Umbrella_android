package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LessonBaseInteractor : BaseInteractor {

    suspend fun fetchCategories(): List<Module>

    suspend fun fetchCategoryBy(id: Long): Module?

    suspend fun fetchSubject(id: Long): Subject?

    suspend fun fetchChildBy(id: Long): Difficulty?

    suspend fun fetchMarkdownBySubject(subjectId: Long): Markdown?

    suspend fun fetchMarkdownByModule(moduleId: Long): Markdown?

    suspend fun fetchMarkdownsBy(id: Long): List<Markdown>

    suspend fun fetchTopicPreferredBy(difficultyId: Long): TopicPreferred?
}