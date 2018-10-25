package org.secfirst.umbrella.whitelabel.data.database.difficulty

import javax.inject.Inject

class DifficultyRepository @Inject constructor(private val diffDao: DifficultyDao) : DifficultyRepo {

    override suspend fun loadSubjectByModule(moduleId: Long) = diffDao.getSubjectByModule(moduleId)

    override suspend fun loadChildBy(id: Long) = diffDao.getChildBy(id)

    override suspend fun loadSubjectBy(subjectId: Long) = diffDao.getSubjectBy(subjectId)

    override suspend fun saveTopicPreferred(difficultyPreferred: DifficultyPreferred) = diffDao.save(difficultyPreferred)
}