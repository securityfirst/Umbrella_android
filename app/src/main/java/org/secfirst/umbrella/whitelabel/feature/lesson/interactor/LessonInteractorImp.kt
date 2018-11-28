package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepo
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LessonInteractorImp @Inject constructor(private val lessonRepo: LessonRepo) : BaseInteractorImp(), LessonBaseInteractor {


    override suspend fun fetchAllFavorites() = lessonRepo.loadAllFavoriteSubjects()

    override suspend fun fetchMarkdownByModule(moduleId: Long) = lessonRepo.loadMarkdownByModule(moduleId)

    override suspend fun fetchMarkdownsBy(sha1ID: String) = lessonRepo.loadMarkdownsBy(sha1ID)

    override suspend fun fetchMarkdownBySubject(id: Long) = lessonRepo.loadMarkdownBySubject(id)

    override suspend fun fetchDifficulty(id: Long) = lessonRepo.loadDifficultyBy(id)

    override suspend fun fetchLesson(id: Long) = lessonRepo.loadLessonBy(id)

    override suspend fun fetchDifficultyPreferredBy(subjectId: Long) = lessonRepo.loadDifficultyPreferredBy(subjectId)

    override suspend fun fetchSubject(id: Long) = lessonRepo.loadSubject(id)

    override suspend fun fetchModules(): List<Module> = lessonRepo.loadAllModules()
}