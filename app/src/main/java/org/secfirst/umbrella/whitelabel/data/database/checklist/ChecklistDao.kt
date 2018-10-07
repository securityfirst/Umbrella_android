package org.secfirst.umbrella.whitelabel.data.database.checklist

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface ChecklistDao {


    suspend fun save(checklistContent: Content) {
        withContext(ioContext) {
            modelAdapter<Content>().save(checklistContent)
        }
    }

    suspend fun getChecklist(id: Long): Checklist? = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.id.`is`(id))
                .querySingle()
    }


}