package org.secfirst.umbrella.whitelabel.feature.difficulty.view

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface DifficultyView : BaseView {

    fun showDifficulties(difficulties: List<Difficulty>, toolbarTitle: String)

    fun startSegment(difficultyIds: List<String>)
}