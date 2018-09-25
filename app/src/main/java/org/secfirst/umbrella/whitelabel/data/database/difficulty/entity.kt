package org.secfirst.umbrella.whitelabel.data.database.difficulty

import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

data class Difficulty(val titleToolbar: String, val items: List<Item>) {

    companion object {
        const val BEGINNER = 1
        const val ADVANCED = 2
        const val EXPERT = 3
        const val COLOR_BEGINNER = "#87BD34"
        const val COLOR_ADVANCED = "#F3BC2B"
        const val COLOR_EXPERT = "#B83657"
    }

    data class Item(val title: String,
                    val description: String,
                    val layoutColor: String,
                    val idReference: Long)
}

fun Subcategory.toDifficult(): Difficulty {
    val items = mutableListOf<Difficulty.Item>()
    val subcategorySorted = this.children.sortedWith(compareBy { it.index })
    subcategorySorted.forEach { child ->
        when (child.index) {
            Difficulty.BEGINNER -> items.add(Difficulty.Item(child.title, child.description, Difficulty.COLOR_BEGINNER, this.id))
            Difficulty.ADVANCED -> items.add(Difficulty.Item(child.title, child.description, Difficulty.COLOR_ADVANCED, this.id))
            Difficulty.EXPERT -> items.add(Difficulty.Item(child.title, child.description, Difficulty.COLOR_EXPERT, this.id))
            else -> {
                items.add(Difficulty.Item(child.title, child.description, Difficulty.COLOR_EXPERT, this.id))
            }
        }
    }
    return Difficulty(this.title, items)
}