package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

data class Difficult(val title: String,
                     val description: String,
                     val idReference: Long,
                     val layoutColor: String,
                     val titleToolbar: String) {

    companion object {
        const val BEGINNER = 1
        const val ADVANCED = 2
        const val EXPERT = 3
        const val COLOR_BEGINNER = "#87BD34"
        const val COLOR_ADVANCED = "#F3BC2B"
        const val COLOR_EXPERT = "#B83657"
    }
}

fun Subcategory.toDifficult(): MutableList<Difficult> {
    val difficulties = mutableListOf<Difficult>()
    val subcategorySorted = this.children.sortedWith(compareBy { it.index })
    subcategorySorted.forEach { child ->
        when (child.index) {
            Difficult.BEGINNER -> difficulties.add(Difficult(child.title, child.description, this.id, Difficult.COLOR_BEGINNER, this.title))
            Difficult.ADVANCED -> difficulties.add(Difficult(child.title, child.description, this.id, Difficult.COLOR_ADVANCED, this.title))
            Difficult.EXPERT -> difficulties.add(Difficult(child.title, child.description, this.id, Difficult.COLOR_EXPERT, this.title))
            else -> {
                difficulties.add(Difficult(child.title, child.description, this.id, Difficult.COLOR_EXPERT, this.title))
            }
        }
    }
    return difficulties
}