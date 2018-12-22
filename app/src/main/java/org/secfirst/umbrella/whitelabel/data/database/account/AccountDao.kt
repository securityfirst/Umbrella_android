package org.secfirst.umbrella.whitelabel.data.database.account

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.BaseDao
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface AccountDao : BaseDao {

    suspend fun saveFeedLocation(feedLocation: FeedLocation) {
        withContext(AppExecutors.ioContext) {
            modelAdapter<FeedLocation>().save(feedLocation)
        }
    }

    suspend fun saveAllFeedSource(feedSources: List<FeedSource>) {
        withContext(AppExecutors.ioContext) {
            modelAdapter<FeedSource>().saveAll(feedSources)
        }
    }

    suspend fun getAllFeedSource(): List<FeedSource> = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(FeedSource::class.java)
                .queryList()
    }

    suspend fun getFeedLocation() = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(FeedLocation::class.java)
                .querySingle()
    }
}