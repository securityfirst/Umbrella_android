package org.secfirst.umbrella.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.difficulty.DifficultyPreferred
import org.secfirst.umbrella.data.database.difficulty.DifficultyPreferred_Table
import org.secfirst.umbrella.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.Markdown_Table
import org.secfirst.umbrella.misc.AppExecutors.Companion.ioContext

interface LessonDao {

    suspend fun getAllLesson(): List<Module> = withContext(ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .queryList()
    }

    suspend fun getDifficultyBySubject(subjectId: String): List<Difficulty> = withContext(ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.subject_id.`is`(subjectId))
                .queryList()
    }

    suspend fun getMarkdownBySubject(subjectId: String): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.subject_id.`is`(subjectId))
                .queryList()
    }

    suspend fun getDifficultyPreferred(subjectId: String): DifficultyPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(DifficultyPreferred::class.java)
                .where(DifficultyPreferred_Table.subjectId.`is`(subjectId))
                .querySingle()
    }

    suspend fun getLessonBy(moduleId: String): Module? = withContext(ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .where(Module_Table.id.`is`(moduleId))
                .querySingle()
    }

    suspend fun getFavoriteMarkdown(): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.favorite.`is`(true))
                .queryList()
    }
}