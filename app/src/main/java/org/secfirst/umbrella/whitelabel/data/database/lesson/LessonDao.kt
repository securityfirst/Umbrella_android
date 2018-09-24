package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory_Table
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

    suspend fun getTopic(subcategoryId: Long): TopicPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(TopicPreferred::class.java)
                .where(TopicPreferred_Table.subcategorySelected_id.`is`(subcategoryId))
                .querySingle()
    }

}