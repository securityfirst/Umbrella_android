package org.secfirst.umbrella.whitelabel.data.database.checklist

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface ChecklistDao {

    suspend fun getChecklist(id: Long): Checklist? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.id.`is`(id))
                .querySingle()
    }
}