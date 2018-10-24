package org.secfirst.umbrella.whitelabel.data.database

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface BaseDao {

    suspend fun loadDifficulty(difficulties: Collection<Difficulty>): Collection<Difficulty> = withContext(AppExecutors.ioContext) {
        val databaseWrapper = FlowManager.getWritableDatabase(AppDatabase::class.java)
        difficulties.forEach { difficulty ->
            difficulty.subject?.let {
                modelAdapter<Subject>().load(it, databaseWrapper)
            }
        }
        return@withContext difficulties
    }
}