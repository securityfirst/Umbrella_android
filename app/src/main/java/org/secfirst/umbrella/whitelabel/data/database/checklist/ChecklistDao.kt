package org.secfirst.umbrella.whitelabel.data.database.checklist

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext


interface ChecklistDao {

    suspend fun save(checklistContent: Content) {
        withContext(ioContext) {
            modelAdapter<Content>().save(checklistContent)
        }
    }

    suspend fun save(checklist: Checklist) {
        withContext(ioContext) {
            modelAdapter<Checklist>().save(checklist)
        }
    }

    suspend fun deleteChecklistContent(checklistContent: Content) {
        withContext(ioContext) {
            modelAdapter<Content>().delete(checklistContent)
        }
    }

    suspend fun deleteChecklist(checklist: Checklist) {
        withContext(ioContext) {
            SQLite.delete()
                    .from(Checklist::class.java)
                    .where(Checklist_Table.id.`is`(checklist.id))
                    .execute()
        }
    }

    suspend fun disable(checklist: Content) {
        withContext(ioContext) {
            modelAdapter<Content>().save(checklist)
        }
    }

    suspend fun getChecklist(checklistId: String): Checklist? = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.id.`is`(checklistId))
                .querySingle()
    }

    suspend fun getAllChecklistFavorite(): List<Checklist> = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.favorite.`is`(true))
                .queryList()
    }

    suspend fun getChecklistCount(): Long = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .queryList().size.toLong()
    }

    suspend fun getAllChecklist(): List<Checklist> = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .queryList()
    }

    suspend fun getSubjectById(subjectId: String) = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.id.`is`(subjectId))
                .querySingle()
    }

    suspend fun getDifficultyById(difficultyId: String): Difficulty? = withContext(ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.id.`is`(difficultyId))
                .querySingle()
    }

    suspend fun getAllChecklistInProgress(): List<Checklist> = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.progress.greaterThanOrEq(1))
                .and(Checklist_Table.favorite.`is`(false))
                .queryList()
    }

    suspend fun getAllCustomChecklistInProgress(): List<Checklist> = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.custom.`is`(true))
                .queryList()
    }
}

