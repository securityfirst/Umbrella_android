package org.secfirst.umbrella.whitelabel.data.database.reader

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.BaseDao
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface ReaderDao : BaseDao{

    suspend fun saveRss(rss: RSS) {
        withContext(ioContext) {
            modelAdapter<RSS>().save(rss)
        }
    }

    suspend fun saveFeedLocation(feedLocation: FeedLocation) {
        withContext(ioContext) {
            modelAdapter<FeedLocation>().save(feedLocation)
        }
    }

    suspend fun saveAllRss(rssList: List<RSS>) {
        withContext(ioContext) {
            modelAdapter<RSS>().saveAll(rssList)
        }
    }

    suspend fun deleteLocation() {
        withContext(ioContext) {
            Delete.table(FeedLocation::class.java)
        }
    }

    suspend fun saveAllFeedSource(feedSources: List<FeedSource>) {
        withContext(ioContext) {
            modelAdapter<FeedSource>().saveAll(feedSources)
        }
    }

    suspend fun delete(rss: RSS) = withContext(ioContext) {
        modelAdapter<RSS>().delete(rss)
    }

    suspend fun getAllRss(): List<RSS> = withContext(ioContext) {
        SQLite.select()
                .from(RSS::class.java)
                .queryList()
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
}