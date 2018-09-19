package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.Markdown
import org.secfirst.umbrella.whitelabel.data.Markdown_Table
import org.secfirst.umbrella.whitelabel.data.database.content.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface LessonDao {

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

    suspend fun getChildBy(subcategoryId: Long, difficultTitle: String): Child = withContext(ioContext) {
        SQLite.select()
                .from(Child::class.java)
                .where(Child_Table.subcategory_id.`is`(subcategoryId), Child_Table.title.`is`(difficultTitle))
                .queryList().last()
    }

    suspend fun getAllMarkdownsBy(subcategoryId: Long): List<Markdown> = withContext(ioContext) {
        SQLite.select()
                .from(Markdown::class.java)
                .where(Markdown_Table.child_id.`is`(subcategoryId))
                .queryList()
    }

}