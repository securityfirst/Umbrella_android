package org.secfirst.umbrella.whitelabel.data.database.lesson

import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Child
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

data class Lesson(var subject: String = "",
                  var pathIcon: String = "",
                  var topics: List<Topic> = listOf()) : ExpandableGroup<Lesson.Topic>(subject, topics) {
    @Parcelize
    class Topic(var title: String = "", var idReference: Long = 0) : Parcelable
}


@Table(database = AppDatabase::class)
data class TopicPreferred(@PrimaryKey(autoincrement = true)
                          var id: Long = 0,
                          @ForeignKey(stubbedRelationship = false)
                          var subcategorySelected: Subcategory? = null,
                          @ForeignKey(stubbedRelationship = false)
                          var childSelected: Child? = null) : BaseModel() {
    constructor(subcategorySelected: Subcategory, childSelected: Child?) : this(0, subcategorySelected, childSelected)
}

fun List<Category>.toLesson(): List<Lesson> {
    val lessons = mutableListOf<Lesson>()
    this.forEach { category ->
        val topics = mutableListOf<Lesson.Topic>()
        category.subcategories.forEach { subcategory ->
            val topic = Lesson.Topic(subcategory.title, subcategory.id)
            topics.add(topic)
        }
        val lesson = Lesson(category.title, category.resourcePath, topics)
        lessons.add(lesson)
    }
    return lessons
}