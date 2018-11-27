package org.secfirst.umbrella.whitelabel.feature.tent

import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView
import java.io.File

interface TentView : BaseView {

    fun isUpdateRepository(res: Boolean) {}

    fun isFetchRepository(res: Boolean) {}

    fun onLoadElementSuccess(files: List<Pair<String, File>>) {}

    fun getCategoryImage(imagePath: String) {}

}