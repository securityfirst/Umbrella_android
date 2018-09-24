package org.secfirst.umbrella.whitelabel.feature.difficulty.view

import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficult
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface DifficultyView : BaseView {

    fun showDifficulties(difficulties: List<Difficult>)
}