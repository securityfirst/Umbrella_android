package org.secfirst.umbrella.whitelabel.feature.segment.view

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface SegmentView : BaseView {

    fun showSegmentDetail(markdown: Markdown) {}

    fun showSegmentsWithDifficulty(difficulties: List<Difficulty>, markdownIndexSelected: Int = -1) {}

    fun showSegments(markdowns: List<Markdown>, markdownIndexSelected: Int = -1) {}

    fun showSegments(markdowns: List<Markdown>, checklist: Checklist?) {}

    fun getTitleToolbar(title: String) {}
}