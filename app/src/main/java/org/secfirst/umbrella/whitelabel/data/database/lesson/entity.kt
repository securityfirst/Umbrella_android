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
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.ADVANCED_COLOR
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.BEGINNER_COLOR
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.EXPERT_COLOR
import org.secfirst.umbrella.whitelabel.data.database.content.Markdown

@Parcelize
data class Difficult(val title: String,
                     val description: String,
                     val idReference: Long,
                     val layoutColor: String,
                     val titleToolbar: String) : Parcelable {

    companion object {
        const val BEGINNER = 1
        const val ADVANCED = 2
        const val EXPERT = 3
        const val BEGINNER_COLOR = "#87BD34"
        const val ADVANCED_COLOR = "#F3BC2B"
        const val EXPERT_COLOR = "#B83657"
    }
}

data class Lesson(var subject: String = "",
                  var pathIcon: String = "",
                  var topics: List<Topic> = listOf()) : ExpandableGroup<Lesson.Topic>(subject, topics) {
    @Parcelize
    class Topic(var title: String = "", var idReference: Long = 0) : Parcelable
}

@Parcelize
data class Segment(var title: String,
                   var index: String,
                   var titleToolbar: String,
                   var idReference: Long) : Parcelable


@Table(database = AppDatabase::class)
data class TopicPreferred(@PrimaryKey(autoincrement = true)
                          var id: Long = 0,
                          @ForeignKey(stubbedRelationship = false)
                          var subcategorySelected: Subcategory? = null,
                          @ForeignKey(stubbedRelationship = false)
                          var childSelected: Child? = null) : BaseModel() {
    constructor(subcategorySelected: Subcategory, childSelected: Child?) : this(0, subcategorySelected, childSelected)
}

fun Subcategory.toDifficult(): MutableList<Difficult> {
    val difficulties = mutableListOf<Difficult>()
    val subcategorySorted = this.children.sortedWith(compareBy { it.index })
    subcategorySorted.forEach { child ->
        when (child.index) {
            Difficult.BEGINNER -> difficulties.add(Difficult(child.title, child.description, this.id, BEGINNER_COLOR, this.title))
            Difficult.ADVANCED -> difficulties.add(Difficult(child.title, child.description, this.id, ADVANCED_COLOR, this.title))
            Difficult.EXPERT -> difficulties.add(Difficult(child.title, child.description, this.id, EXPERT_COLOR, this.title))
            else -> {
                difficulties.add(Difficult(child.title, child.description, this.id, EXPERT_COLOR, this.title))
            }
        }
    }
    return difficulties
}

fun MutableList<Markdown>.toSegment(subcategoryId: Long, titleToolbar: String): MutableList<Segment> {
    val segments = mutableListOf<Segment>()
    this.forEach { markdown ->
        segments.add(Segment(markdown.title, markdown.index, titleToolbar, subcategoryId))
    }
    return segments
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