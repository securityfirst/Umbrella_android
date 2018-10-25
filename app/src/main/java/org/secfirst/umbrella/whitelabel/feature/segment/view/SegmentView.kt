package org.secfirst.umbrella.whitelabel.feature.segment.view

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface SegmentView : BaseView {

    fun showSegments(difficulties: MutableList<Difficulty>) {}

    fun showSegmentDetail(markdown: Markdown) {}
}