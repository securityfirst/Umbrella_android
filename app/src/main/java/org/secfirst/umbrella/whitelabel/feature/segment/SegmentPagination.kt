package org.secfirst.umbrella.whitelabel.feature.segment

import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown

class SegmentPagination(private val markdowns: MutableList<Markdown>) {

    private val limitPage = 6

    fun nextPage(): List<Markdown> {
        val pages = mutableListOf<Markdown>()

        if (markdowns.size <= limitPage) {
            pages.addAll(markdowns)
            markdowns.clear()
        } else {
            if (markdowns.isNotEmpty()) {
                (0..limitPage).forEach { pages.add(markdowns[it]) }
                (0..limitPage).forEach { _ -> markdowns.removeAt(0) }
            }
        }
        return pages.sortedWith(compareBy { it.index.toInt() })
    }
}