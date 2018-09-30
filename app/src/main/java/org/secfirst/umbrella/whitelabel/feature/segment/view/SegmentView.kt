package org.secfirst.umbrella.whitelabel.feature.segment.view

import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface SegmentView : BaseView {

    fun showSegments(segments: List<Segment>)

    fun showSegmentDetail(markdown: Markdown)
}