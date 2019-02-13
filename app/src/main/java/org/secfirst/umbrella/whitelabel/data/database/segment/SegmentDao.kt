package org.secfirst.umbrella.whitelabel.data.database.segment

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.BaseDao
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject_Table
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

    suspend fun save(subjectId: String, difficulty: Difficulty) {
        withContext(ioContext) {
            modelAdapter<DifficultyPreferred>().save(DifficultyPreferred(subjectId, difficulty))
        }
    }

    suspend fun getDifficultyBySubjectId(subjectId: String): List<Difficulty> = withContext(ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.subject_id.`is`(subjectId))
                .queryList()
    }

    suspend fun getSubjectByRootDir(rootDir: String): Subject? = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.rootDir.`is`(rootDir))
                .querySingle()
    }

    suspend fun getModuleByRootDir(rootDir: String) =
            withContext(ioContext) {
                SQLite.select()
                        .from(Module::class.java)
                        .where(Module_Table.rootDir.`is`(rootDir))
                        .querySingle()
            }

}