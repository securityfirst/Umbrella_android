package org.secfirst.umbrella.feature.segment

import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.sortByIndex
import org.secfirst.umbrella.data.disk.Template

class SegmentPagination(private val markdowns: MutableList<Markdown>) {

    private val limitPage = 6

    fun nextPage(): List<Markdown> {
        var glossary = false
        val pages = mutableListOf<Markdown>()
        markdowns.forEach {
            if (it.id.contains(Template.GLOSSARY.value)) {
                glossary = true
            }
        }

        if (markdowns.size <= limitPage) {
            pages.addAll(markdowns)
            markdowns.clear()
        } else {
            if (markdowns.isNotEmpty()) {
                (0..limitPage).forEach { pages.add(markdowns[it]) }
                (0..limitPage).forEach { _ -> markdowns .removeAt(0) }
            }
        }
        if (glossary){
            return pages
        }

        return pages.sortByIndex()
    }
}