package org.secfirst.umbrella.whitelabel.data.database.lesson

import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.content.Category

data class Lesson(var idReference: Long,
                  var subject: String = "",
                  var pathIcon: String = "",
                  var topics: List<Topic> = listOf()) : ExpandableGroup<Lesson.Topic>(subject, topics) {
    @Parcelize
    class Topic(var title: String = "", var idReference: Long = 0) : Parcelable

    companion object {
        const val GLOSSARY = "Glossary"
    }
}


@Table(database = AppDatabase::class)
data class TopicPreferred(@PrimaryKey
                          var subcategoryId: Long? = 0,
                          @PrimaryKey
                          var childId: Long? = 0) : BaseModel()

fun List<Category>.toLesson(): List<Lesson> {
    val lessons = mutableListOf<Lesson>()
    val categoriesSorted = this.sortedWith(compareBy { it.index })
    categoriesSorted.forEach { category ->
        val topics = mutableListOf<Lesson.Topic>()
        category.subcategories.forEach { subcategory ->
            val topic = Lesson.Topic(subcategory.title, subcategory.id)
            topics.add(topic)
        }
        val lesson = Lesson(category.id, category.title, category.resourcePath, topics)
        lessons.add(lesson)
    }
    return lessons
}