package org.secfirst.umbrella.whitelabel.data.disk

import java.io.File


interface TentRepo {

    suspend fun fetch(): Boolean

    suspend fun loadElementsFile(): List<File>

    fun loadFile(): List<File>

    fun loadCategoryImage(imgName: String): String

}