package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.whitelabel.data.database.difficulty.DifficultyPreferred_Table
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface LessonDao {

    suspend fun getAllLesson(): List<Module> = withContext(ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .queryList()
    }

    suspend fun getSubject(id: String): Subject? = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getDifficultyBy(id: String): Difficulty? = withContext(ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getDifficultyBySubject(subjectId: String): List<Difficulty> = withContext(ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.subject_id.`is`(subjectId))
                .queryList()
    }
    suspend fun getMarkdownBySubject(subjectSh1ID: String): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.id.`is`(subjectSh1ID))
                .queryList()
    }

    suspend fun getMarkdownByModule(sha1ID: String): Markdown? = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.module_id.`is`(sha1ID))
                .querySingle()
    }

    suspend fun getMarkdowns(sha1ID: String): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.id.`is`(sha1ID))
                .queryList()
    }

    suspend fun getDifficultyPreferred(subjectSh1ID: String): DifficultyPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(DifficultyPreferred::class.java)
                .where(DifficultyPreferred_Table.subjectId.`is`(subjectSh1ID))
                .querySingle()
    }

    suspend fun getLessonBy(sha1ID: String): Module? = withContext(ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .where(Module_Table.id.`is`(sha1ID))
                .querySingle()
    }

    suspend fun getFavoriteMarkdown(): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.favorite.`is`(true))
                .queryList()
    }
}