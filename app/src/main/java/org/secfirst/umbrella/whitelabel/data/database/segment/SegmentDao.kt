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

    suspend fun save(subjectSh1ID: String, difficulty: Difficulty) {
        withContext(ioContext) {
            modelAdapter<DifficultyPreferred>().save(DifficultyPreferred(subjectSh1ID, difficulty))
        }
    }


    suspend fun getMarkdowns(subjectSh1ID: String): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.subject_path.`is`(subjectSh1ID))
                .queryList()

    }

    suspend fun getSubject(sh1ID: String): Subject? = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.path.`is`(sh1ID))
                .querySingle()
    }

    suspend fun getModule(sh1ID: String): Module? = withContext(ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .where(Module_Table.path.`is`(sh1ID))
                .querySingle()
    }
}