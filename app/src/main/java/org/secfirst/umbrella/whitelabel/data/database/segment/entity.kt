package org.secfirst.umbrella.whitelabel.data.database.segment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.content.Markdown

@Parcelize
data class Segment(var toolbarTitle: String,
                   var subcategoryId: Long,
                   var items: List<Item> = listOf()) : Parcelable {
    @Parcelize
    data class Item(var indexItem: String, var title: String) : Parcelable
}

fun MutableList<Markdown>.toSegment(subcategoryId: Long, title: String): Segment {
    val items = mutableListOf<Segment.Item>()
    this.forEach { markdown ->
        items.add(Segment.Item(markdown.index, markdown.title))
    }
    return Segment(title, subcategoryId, items)
}