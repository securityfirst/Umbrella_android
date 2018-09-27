package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface LessonDao {


    suspend fun getAllCategory(): List<Category> = withContext(ioContext) {
        SQLite.select()
                .from(Category::class.java)
                .queryList()
    }

    suspend fun getSubcategoryBy(id: Long): Subcategory? = withContext(ioContext) {
        SQLite.select()
                .from(Subcategory::class.java)
                .where(Subcategory_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getChildBy(id: Long): Child? = withContext(ioContext) {
        SQLite.select()
                .from(Child::class.java)
                .where(Child_Table.id.`is`(id))
                .querySingle()
    }

    suspend fun getTopic(subcategoryId: Long): TopicPreferred? = withContext(ioContext) {
        SQLite.select()
                .from(TopicPreferred::class.java)
                .where(TopicPreferred_Table.subcategoryId.`is`(subcategoryId))
                .querySingle()
    }

    suspend fun getCategoryBy(id: Long): Category? = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Category::class.java)
                .where(Category_Table.id.`is`(id))
                .querySingle()
    }

}