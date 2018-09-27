package org.secfirst.umbrella.whitelabel.feature.difficulty.view

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface DifficultyView : BaseView {

    fun showDifficulties(difficulty: Difficulty)
    fun startSegment(segments: List<Segment>)
}