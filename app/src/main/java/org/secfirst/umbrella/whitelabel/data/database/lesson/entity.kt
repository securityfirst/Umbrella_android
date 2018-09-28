package org.secfirst.umbrella.whitelabel.data.database.lesson

import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.BaseModel
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty

data class Lesson(var moduleId: Long,
                  var moduleTitle: String = "",
                  var pathIcon: String = "",
                  var topics: List<Subject> = listOf()) : ExpandableGroup<Subject>(moduleTitle, topics) {

    companion object {
        const val GLOSSARY = "Glossary"
    }
}

@Table(database = AppDatabase::class)
data class TopicPreferred(@PrimaryKey(autoincrement = true)
                          var id: Long = 0,
                          @ForeignKey
                          var subject: Subject? = null,
                          @ForeignKey
                          var difficulty: Difficulty? = null) : BaseModel() {
    constructor(subject: Subject?, difficulty: Difficulty?) : this(0, subject, difficulty)
}

fun List<Module>.toLesson(): List<Lesson> {
    val lessons = mutableListOf<Lesson>()
    val moduleSorted = this.sortedWith(compareBy { it.index })
    moduleSorted.forEach { module ->
        val lesson = Lesson(module.id, module.title, module.resourcePath, module.subjects)
        lessons.add(lesson)
    }
    return lessons
}