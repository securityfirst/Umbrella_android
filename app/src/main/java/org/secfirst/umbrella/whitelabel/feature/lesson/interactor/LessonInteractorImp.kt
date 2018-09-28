package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepo
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LessonInteractorImp @Inject constructor(private val lessonRepo: LessonRepo) : BaseInteractorImp(), LessonBaseInteractor {

    override suspend fun fetchChildBy(id: Long) = lessonRepo.loadChildBy(id)

    override suspend fun fetchCategoryBy(id: Long) = lessonRepo.loadCategoryBy(id)

    override suspend fun fetchTopicPreferredBy(subcategory: Long): TopicPreferred? = lessonRepo.loadTopicPreferredBy(subcategory)

    override suspend fun fetchSubcategoryBy(id: Long) = lessonRepo.loadSubcategoryBy(id)

    override suspend fun fetchCategories(): List<Module> = lessonRepo.loadAllCategories()
}