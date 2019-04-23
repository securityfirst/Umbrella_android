package org.secfirst.umbrella.data.database.difficulty

import javax.inject.Inject

class DifficultyRepository @Inject constructor(private val diffDao: DifficultyDao) : DifficultyRepo {

    override suspend fun loadSubjectByModule(moduleSha1ID: String) = diffDao.getSubjectByModule(moduleSha1ID)

    override suspend fun loadDifficultyBy(sha1ID: String) = diffDao.getDifficultyBy(sha1ID)

    override suspend fun loadSubjectBy(subjectSha1ID: String) = diffDao.getSubjectBy(subjectSha1ID)

    override suspend fun saveTopicPreferred(difficultyPreferred: DifficultyPreferred) = diffDao.save(difficultyPreferred)
}