package org.secfirst.umbrella.whitelabel.data.database.segment

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface SegmentDao {

    suspend fun getSubcategoryBy(id: Long): Subcategory = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Subcategory::class.java)
                .where(Subcategory_Table.id.`is`(id))
                .queryList().last()
    }
}