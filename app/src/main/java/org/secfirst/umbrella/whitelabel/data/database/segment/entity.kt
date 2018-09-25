package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.content.Markdown


data class Segment(var toolbarTitle: String,
                   var idReference: Long,
                   var items: List<Item> = listOf()) {

    data class Item(var indexItem: String, var title: String)
}

fun MutableList<Markdown>.toSegment(subcategoryId: Long, title: String): Segment {
    val items = mutableListOf<Segment.Item>()
    this.forEach { markdown ->
        items.add(Segment.Item(markdown.index, markdown.title))
    }
    return Segment(title, subcategoryId, items)
}