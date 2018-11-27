package org.secfirst.umbrella.whitelabel.data.disk

import java.io.File


interface TentRepo {

    suspend fun fetchRepository(): Boolean

    suspend fun updateRepository(): Boolean

    suspend fun loadElementsFile(): List<Pair<String,File>>

    fun loadFile(): List<Pair<String,File>>

    fun loadCategoryImage(imgName: String): String

}