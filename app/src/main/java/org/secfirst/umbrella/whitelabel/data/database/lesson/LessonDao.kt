package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.*
import org.secfirst.umbrella.whitelabel.data.database.content.Markdown
import org.secfirst.umbrella.whitelabel.data.disk.Markdown_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface LessonDao {

    suspend fun save(topicPreferred: TopicPreferred) {
        withContext(ioContext) {
            modelAdapter<TopicPreferred>().insert(topicPreferred)
        }
    }
    suspend fun getAllCategory(): List<Category> = withContext(ioContext) {
        SQLite.select()
                .from(Category::class.java)
                .queryList()
    }

    suspend fun getSubcategoryBy(id: Long): Subcategory = withContext(ioContext) {
        SQLite.select()
                .from(Subcategory::class.java)
                .where(Subcategory_Table.id.`is`(id))
                .queryList().last()
    }

    suspend fun getChildBy(subcategoryId: Long): Child? = withContext(ioContext) {
        SQLite.select()
                .from(Child::class.java)
                .where(Child_Table.subcategory_id.`is`(subcategoryId))
                .querySingle()
    }

    suspend fun getAllMarkdownsBy(subcategoryId: Long): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.child_id.`is`(subcategoryId))
                .queryList()
    }

    suspend fun getTopic(subcategoryId: Long): TopicPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(TopicPreferred::class.java)
                .where(TopicPreferred_Table.subcategorySelected_id.`is`(subcategoryId))
                .querySingle()
    }

}