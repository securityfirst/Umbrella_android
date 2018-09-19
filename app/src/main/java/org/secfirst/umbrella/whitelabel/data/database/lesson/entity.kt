package org.secfirst.umbrella.whitelabel.data.database.lesson

import android.os.Parcelable
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.ADVANCED_COLOR
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.BEGINNER_COLOR
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult.Companion.EXPERT_COLOR

@Parcelize
data class Difficult(val title: String, val description: String, val layoutColor: String, val titleToolbar: String) : Parcelable {
    companion object {
        const val BEGINNER = 1
        const val ADVANCED = 2
        const val EXPERT = 3
        const val BEGINNER_COLOR = "#87BD34"
        const val ADVANCED_COLOR = "#F3BC2B"
        const val EXPERT_COLOR = "#B83657"
    }
}

class Lesson(subject: String, var pathIcon: String, topics: List<Topic>) : ExpandableGroup<Lesson.Topic>(subject, topics) {
    @Parcelize
    class Topic(var title: String = "", var idReference: Long = 0) : Parcelable
}

fun Subcategory.toDifficult(): MutableList<Difficult> {
    val difficulties = mutableListOf<Difficult>()
    val subcategorySorted = this.children.sortedWith(compareBy { it.index })
    subcategorySorted.forEach { child ->
        when (child.index) {
            Difficult.BEGINNER -> difficulties.add(Difficult(child.title, child.description, BEGINNER_COLOR, this.title))
            Difficult.ADVANCED -> difficulties.add(Difficult(child.title, child.description, ADVANCED_COLOR, this.title))
            Difficult.EXPERT -> difficulties.add(Difficult(child.title, child.description, EXPERT_COLOR, this.title))
            else -> {
                difficulties.add(Difficult(child.title, child.description, EXPERT_COLOR, this.title))
            }
        }
    }
    return difficulties
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