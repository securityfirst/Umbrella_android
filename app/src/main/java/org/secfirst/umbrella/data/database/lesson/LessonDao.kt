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
        val modules = SQLite.select()
                .from(ModuleModelView::class.java)
                .orderBy(ModuleModelView_ViewTable.index, true)
                .queryList()
        val subjects = SQLite.select()
                .from(SubjectModelView::class.java)
                .where(SubjectModelView_ViewTable.module_id.`in`(modules.map { it.id }))
                .orderBy(SubjectModelView_ViewTable.index, true)
                .queryList()
        val markdowns = SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.module_id.`in`(modules.map { it.id }))
                .orderBy(Markdown_Table.index, true)
                .queryList()
        modules.map {
            it.toModule().apply {
                this.subjects = subjects.filter { it.module_id == this.id }.map { it.toSubject() }.toMutableList()
                this.markdowns = markdowns.filter { it.module!!.id == this.id }.toMutableList()
            }
        }
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