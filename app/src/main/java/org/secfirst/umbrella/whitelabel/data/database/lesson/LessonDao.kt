package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Module_Table
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.content.Subject_Table
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface LessonDao {


    suspend fun getAllCategory(): List<Module> = withContext(ioContext) {
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

    suspend fun getChildBy(id: Long): Difficulty? = withContext(ioContext) {
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

    suspend fun getTopicPreferred(difficultyId: Long): TopicPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(TopicPreferred::class.java)
                .where(TopicPreferred_Table.difficulty_id.`is`(difficultyId))
                .querySingle()
    }

    suspend fun getCategoryBy(id: Long): Module? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .where(Module_Table.id.`is`(id))
                .querySingle()
    }

}