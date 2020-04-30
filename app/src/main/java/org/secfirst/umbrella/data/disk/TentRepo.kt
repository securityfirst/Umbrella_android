package org.secfirst.umbrella.data.disk

import java.io.File


interface TentRepo {

    suspend fun fetchRepository(url: String): Boolean

    suspend fun updateRepository(): List<Pair<String, File>>

    suspend fun loadElementsFile(path : String): List<File>

    fun loadFormFile(path : String): List<File>
}