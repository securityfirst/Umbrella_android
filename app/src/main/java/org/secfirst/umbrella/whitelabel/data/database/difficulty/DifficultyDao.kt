package org.secfirst.umbrella.whitelabel.data.database.difficulty

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.content.Subject_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface DifficultyDao {

    suspend fun save(topicPreferred: TopicPreferred) {
        withContext(AppExecutors.ioContext) {
            modelAdapter<TopicPreferred>().insert(topicPreferred)
        }
    }

    suspend fun getSubcategoryBy(id: Long): Subject? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getChildBy(id: Long): Difficulty? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.id.`is`(id))
                .querySingle()
    }
}