package org.secfirst.umbrella.whitelabel.data.database.checklist

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
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

    suspend fun getChecklist(sha1ID: String): Checklist? = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.sha1ID.`is`(sha1ID))
                .querySingle()
    }

    suspend fun getChecklistProgressDone(): List<Checklist> = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.progress.`is`(100))
                .queryList()
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

    suspend fun getSubjectById(subjectId: Long) = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.id.`is`(subjectId))
                .querySingle()
    }

    suspend fun getDifficultyById(difficultyId: Long): Difficulty = withContext(ioContext) {
        val result = SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.id.`is`(difficultyId))
                .querySingle()
        return@withContext result!!

    }

    suspend fun getAllChecklistInProgress(): List<Checklist> = withContext(ioContext) {
        SQLite.select()
                .from(Checklist::class.java)
                .where(Checklist_Table.progress.greaterThanOrEq(1))
                .and(Checklist_Table.favorite.`is`(false))
                .queryList()
    }
}

