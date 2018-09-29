package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred

interface DifficultyRepo {

    suspend fun loadChildBy(id: Long): Difficulty?

    suspend fun loadSubjectBy(subjectId: Long): Subject?

    suspend fun loadSubjectByModule(moduleId: Long): Subject?

    suspend fun saveTopicPreferred(topicPreferred: TopicPreferred)
}