package org.secfirst.umbrella.whitelabel.data.database.segment

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.BaseDao
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.misc.AppExecutors
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface SegmentDao : BaseDao {

    suspend fun save(markdown: Markdown) {
        withContext(ioContext) {
            modelAdapter<Markdown>().save(markdown)
        }
    }

    suspend fun save(checklist: Checklist) {
        withContext(ioContext) {
            modelAdapter<Checklist>().save(checklist)
        }
    }

    suspend fun save(subjectSh1ID: String, difficulty: Difficulty) {
        withContext(ioContext) {
            modelAdapter<DifficultyPreferred>().save(DifficultyPreferred(subjectSh1ID, difficulty))
        }
    }

    suspend fun getMarkdowns(subjectSh1ID: String): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.subject_id.`is`(subjectSh1ID))
                .queryList()

    }

    suspend fun getMarkdownBy(moduleId: String): List<Markdown> = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.favorite.`is`(true))
                .queryList()
    }

    suspend fun getMarkdownFromDifficulty(difficultyId: String): List<Markdown> = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.difficulty_id.`is`(difficultyId))
                .queryList()
    }
}