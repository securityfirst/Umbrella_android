package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LessonInteractorImp @Inject constructor(private val lessonRepo: LessonRepo) : BaseInteractorImp(), LessonBaseInteractor {

    override suspend fun fetchCategoryBy(id: Long) = lessonRepo.loadCategoryBy(id)

    override suspend fun fetchLesson(): List<Category> = lessonRepo.loadAllCategories()
}