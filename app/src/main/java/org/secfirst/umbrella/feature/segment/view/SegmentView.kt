package org.secfirst.umbrella.feature.segment.view

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.view.BaseView

interface SegmentView : BaseView {

    fun showSegmentDetail(markdown: Markdown) {}

    fun showSegmentsWithDifficulty(difficulties: List<Difficulty>, markdownIndexSelected: Int = -1) {}

    fun showSegments(markdowns: List<Markdown>, markdownIndexSelected: Int = -1) {}

    fun showSegments(markdowns: List<Markdown>, checklist: Checklist?) {}

    fun getTitleToolbar(title: String) {}
}