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

    suspend fun getSubcategoryBy(id: Long): Subject? = withContext(ioContext) {
        SQLite.select()
                .from(Subject::class.java)
                .where(Subject_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getChildBy(id: Long): Difficulty? = withContext(ioContext) {
        SQLite.select()
                .from(Difficulty::class.java)
                .where(Difficulty_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getMarkdown(id: Long): Markdown? = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getTopic(subcategoryId: Long): TopicPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(TopicPreferred::class.java)
                .where(TopicPreferred_Table.subject_id.`is`(subcategoryId))
                .querySingle()
    }

    suspend fun getCategoryBy(id: Long): Module? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Module::class.java)
                .where(Module_Table.id.`is`(id))
                .querySingle()
    }

}