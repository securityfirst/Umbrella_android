package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
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

    suspend fun getSubject(subjectId: Long): Subject? = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.id.`is`(subjectId))
                .querySingle()
    }

    suspend fun getDifficultyBy(id: Long): Difficulty? = withContext(ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getMarkdownBySubject(subjectId: Long): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.subject_id.`is`(subjectId))
                .queryList()
    }

    suspend fun getMarkdownByModule(moduleId: Long): Markdown? = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.module_id.`is`(moduleId))
                .querySingle()
    }

    suspend fun getMarkdowns(id: Long): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.id.`is`(id))
                .queryList()
    }

    suspend fun getDifficultyPreferred(subjectId: Long): DifficultyPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(DifficultyPreferred::class.java)
                .where(DifficultyPreferred_Table.subjectId.`is`(subjectId))
                .querySingle()
    }

    suspend fun getLessonBy(id: Long): Module? = withContext(ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .where(Module_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getFavoriteMarkdown(): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.favorite.`is`(true))
                .queryList()
    }
}