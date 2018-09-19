package org.secfirst.umbrella.whitelabel.data.database.lesson

import android.os.Parcelable
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.Markdown
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.ADVANCED_COLOR
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.BEGINNER_COLOR
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.EXPERT_COLOR

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
                   var description: String,
                   var layoutColor: String,
                   var idReference: Long) : Parcelable

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

fun List<Markdown>.toSegment(): MutableList<Segment> {
    val segments = mutableListOf<Segment>()
    //val childSorted = this.children.sortedWith(compareBy { it.index })
    this.forEach { markdown ->
        val title = markdown.text.substringAfter(Markdown.TAG_TITLE)
        val index = markdown.text.substringAfter(Markdown.TAG_INDEX)
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