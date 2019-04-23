package org.secfirst.umbrella.feature.difficulty.view

import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.feature.base.view.BaseView

interface DifficultyView : BaseView {

    fun showDifficulties(difficulties: List<Difficulty>, toolbarTitle: String)

    fun startSegment(difficultyIds: List<String>)
}