package org.secfirst.umbrella.whitelabel.data.database.segment

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface SegmentDao {

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

    suspend fun save(subjectId: Long, difficulty: Difficulty) {
        withContext(ioContext) {
            modelAdapter<DifficultyPreferred>().save(DifficultyPreferred(subjectId, difficulty))
        }
    }


    suspend fun getMarkdowns(subjectId: Long): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.subject_id.`is`(subjectId))
                .queryList()

    }

    suspend fun getSubject(id: Long): Subject? = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getModule(id: Long): Module? = withContext(ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .where(Module_Table.id.`is`(id))
                .querySingle()
    }
}