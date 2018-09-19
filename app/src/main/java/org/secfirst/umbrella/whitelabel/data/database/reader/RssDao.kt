package org.secfirst.umbrella.whitelabel.data.database.reader

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface RssDao {

    suspend fun save(rss: RSS) {
        withContext(ioContext) {
            modelAdapter<RSS>().save(rss)
        }
    }

    suspend fun saveAll(rssList: List<RSS>) {
        withContext(ioContext) {
            modelAdapter<RSS>().saveAll(rssList)
        }
    }

    suspend fun delete(rss: RSS) = withContext(ioContext) {
        modelAdapter<RSS>().delete(rss)
    }

    suspend fun getAll(): List<RSS> {
        return withContext(ioContext) {
            SQLite.select()
                    .from(RSS::class.java)
                    .queryList()
        }
    }
}