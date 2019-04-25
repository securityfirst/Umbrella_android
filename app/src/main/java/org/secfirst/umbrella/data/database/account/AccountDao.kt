package org.secfirst.umbrella.data.database.account

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.BaseDao
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.misc.AppExecutors.Companion.ioContext

interface AccountDao : BaseDao {

    suspend fun saveFeedLocation(feedLocation: FeedLocation) {
        withContext(ioContext) {
            modelAdapter<FeedLocation>().save(feedLocation)
        }
    }

    suspend fun saveAllFeedSource(feedSources: List<FeedSource>) {
        withContext(ioContext) {
            modelAdapter<FeedSource>().saveAll(feedSources)
        }
    }

    suspend fun getAllFeedSource(): List<FeedSource> = withContext(ioContext) {
        SQLite.select()
                .from(FeedSource::class.java)
                .queryList()
    }

    suspend fun getFeedLocation() = withContext(ioContext) {
        SQLite.select()
                .from(FeedLocation::class.java)
                .querySingle()
    }

    suspend fun deleteMainContent(): Boolean {
        var res = false
        withContext(ioContext) {
            res = try {
                FlowManager.getDatabase(AppDatabase.NAME).reset()
                true
            } catch (e: Exception) {
                false
            }
        }
        return res
    }
}