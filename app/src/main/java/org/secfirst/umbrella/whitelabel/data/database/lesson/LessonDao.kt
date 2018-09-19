package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Category_Table
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors

interface LessonDao {

    suspend fun getAllLesson(): List<Category> = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Category::class.java)
                .queryList()
    }

    suspend fun getCategoryBy(id: Long): Subcategory = withContext(AppExecutors.ioContext) {
        SQLite.select()
                .from(Subcategory::class.java)
                .where(Subcategory_Table.id.`is`(id))
                .queryList().last()
    }

}