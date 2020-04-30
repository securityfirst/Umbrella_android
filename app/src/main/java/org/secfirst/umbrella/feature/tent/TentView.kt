package org.secfirst.umbrella.feature.tent

import org.secfirst.umbrella.feature.base.view.BaseView
import java.io.File

interface TentView : BaseView {

    fun isUpdateRepository(pairFiles: List<Pair<String, File>>) {
        println(pairFiles.forEach { it.second.absolutePath })
    }

    fun isFetchRepository(res: Boolean) {}

    fun onLoadElementSuccess(files: List<File>) {}

    fun getCategoryImage(imagePath: String) {}

}