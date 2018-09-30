package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LessonInteractorImp @Inject constructor(private val lessonRepo: LessonRepo) : BaseInteractorImp(), LessonBaseInteractor {

    override suspend fun fetchMarkdownByModule(moduleId: Long) = lessonRepo.loadMarkdownByModule(moduleId)

    override suspend fun fetchMarkdownsBy(id: Long) = lessonRepo.loadMarkdownsBy(id)

    override suspend fun fetchMarkdownBySubject(id: Long) = lessonRepo.loadMarkdownBySubject(id)

    override suspend fun fetchChildBy(id: Long) = lessonRepo.loadChildBy(id)

    override suspend fun fetchCategoryBy(id: Long) = lessonRepo.loadCategoryBy(id)

    override suspend fun fetchTopicPreferredBy(difficultyId: Long) = lessonRepo.loadTopicPreferredBy(difficultyId)

    override suspend fun fetchSubject(id: Long) = lessonRepo.loadSubcategoryBy(id)

    override suspend fun fetchCategories(): List<Module> = lessonRepo.loadAllCategories()
}