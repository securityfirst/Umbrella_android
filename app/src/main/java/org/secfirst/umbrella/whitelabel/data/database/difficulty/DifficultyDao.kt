package org.secfirst.umbrella.whitelabel.data.database.difficulty

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface DifficultyDao {

    suspend fun save(difficultyPreferred: DifficultyPreferred) {
        withContext(AppExecutors.ioContext) {
            modelAdapter<DifficultyPreferred>().save(difficultyPreferred)
        }
    }

    suspend fun getSubjectByModule(moduleSha1ID: String): Subject? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.module_sh1ID.`is`(moduleSha1ID))
                .querySingle()
    }

    suspend fun getSubjectBy(subjectSha1ID: String): Subject? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.sh1ID.`is`(subjectSha1ID))
                .querySingle()
    }

    suspend fun getDifficultyBy(sha1ID : String): Difficulty? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.sha1ID.`is`(sha1ID))
                .querySingle()
    }
}