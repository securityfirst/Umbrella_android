package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepo
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LessonInteractorImp @Inject constructor(private val lessonRepo: LessonRepo) : BaseInteractorImp(), LessonBaseInteractor {

    override suspend fun insertTopicPreferred(topicPreferred: TopicPreferred) = lessonRepo.saveTopicPreffered(topicPreferred)

    override suspend fun fetchTopicPreferredBy(subcategory: Long): TopicPreferred? = lessonRepo.loadTopicPreferredBy(subcategory)

    override suspend fun fetchChildBy(subcategory: Long) = lessonRepo.loadChildBy(subcategory)

    override suspend fun fetchAllMarkdownsBy(subcategory: Long) = lessonRepo.loadAllMarkdowns(subcategory)

    override suspend fun fetchSubcategoryBy(categoryId: Long) = lessonRepo.loadSubcategoryBy(categoryId)

    override suspend fun fetchCategories(): List<Category> = lessonRepo.loadAllCategories()
}