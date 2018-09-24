package org.secfirst.umbrella.whitelabel.data.database.difficulty

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface DifficultyDao {

    suspend fun save(topicPreferred: TopicPreferred) {
        withContext(AppExecutors.ioContext) {
            modelAdapter<TopicPreferred>().insert(topicPreferred)
        }
    }

    suspend fun getSubcategoryBy(id: Long): Subcategory = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Subcategory::class.java)
                .where(Subcategory_Table.id.`is`(id))
                .queryList().last()
    }
}