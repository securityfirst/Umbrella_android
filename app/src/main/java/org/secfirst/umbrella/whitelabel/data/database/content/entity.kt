package org.secfirst.umbrella.whitelabel.data.database.content


import android.content.Context
import android.content.Intent
import android.net.Uri
import org.apache.commons.text.WordUtils
import org.secfirst.advancedsearch.models.SearchResult
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module

class ContentData(val modules: MutableList<Module> = mutableListOf())

fun Content.toSearchResult(): SearchResult {
    val segments = this.checklist?.id.orEmpty().split("/")
    return SearchResult("${WordUtils.capitalizeFully(segments[1])} - ${WordUtils.capitalizeFully(segments[3])}", this.check
    ) { c: Context -> c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("umbrella://checklists/${this.checklist?.id}")))
    }
}