package org.secfirst.umbrella.whitelabel.data.disk

import java.io.File


interface TentRepo {

    suspend fun fetchRepository(url: String): Boolean

    suspend fun updateRepository(): List<Pair<String, File>>

    suspend fun loadElementsFile(): List<Pair<String, File>>

    fun loadFile(): List<Pair<String, File>>

    fun loadFormFile(): List<Pair<String, File>>
}