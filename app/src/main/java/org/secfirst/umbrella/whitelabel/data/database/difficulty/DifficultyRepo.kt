package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject

interface DifficultyRepo {

    suspend fun loadDifficultyBy(sha1ID : String): Difficulty?

    suspend fun loadSubjectBy(subjectSha1ID : String): Subject?

    suspend fun loadSubjectByModule(moduleSha1ID : String): Subject?

    suspend fun saveTopicPreferred(difficultyPreferred: DifficultyPreferred)
}