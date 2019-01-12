package org.secfirst.umbrella.whitelabel.feature.lesson.interactor

import org.secfirst.umbrella.whitelabel.data.database.lesson.LessonRepo
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LessonInteractorImp @Inject constructor(private val lessonRepo: LessonRepo) : BaseInteractorImp(), LessonBaseInteractor {

    override suspend fun fetchDifficultyBySubject(subjectId: String) = lessonRepo.loadDifficultyBySubject(subjectId)

    override suspend fun fetchAllFavorites() = lessonRepo.loadAllFavoriteSubjects()

    override suspend fun fetchMarkdownByModule(moduleSha1ID: String) = lessonRepo.loadMarkdownByModule(moduleSha1ID)

    override suspend fun fetchMarkdownsBy(sha1ID: String) = lessonRepo.loadMarkdownsBy(sha1ID)

    override suspend fun fetchMarkdownBySubject(subjectSha1ID: String) = lessonRepo.loadMarkdownBySubject(subjectSha1ID)

    override suspend fun fetchDifficulty(sha1ID: String) = lessonRepo.loadDifficultyBy(sha1ID)

    override suspend fun fetchLesson(sha1ID: String) = lessonRepo.loadLessonBy(sha1ID)

    override suspend fun fetchDifficultyPreferredBy(subjectSha1ID: String) = lessonRepo.loadDifficultyPreferredBy(subjectSha1ID)

    override suspend fun fetchSubject(sha1ID: String) = lessonRepo.loadSubject(sha1ID)

    override suspend fun fetchModules(): List<Module> = lessonRepo.loadAllModules()
}