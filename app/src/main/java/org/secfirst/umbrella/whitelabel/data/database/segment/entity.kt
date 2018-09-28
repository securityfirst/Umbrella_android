package org.secfirst.umbrella.whitelabel.data.database.segment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.whitelabel.data.database.content.Markdown


@Parcelize
data class Segment(var toolbarTitle: String,
                   var subjectId: Long,
                   var markdowns: List<Markdown> = listOf()) : Parcelable

fun MutableList<Markdown>.toSegment(subcategoryId: Long, title: String): Segment {
    val markdowns = mutableListOf<Markdown>()
    this.forEach { markdown ->
        markdowns.add(markdown)
    }
    return Segment(title, subcategoryId, markdowns)
}