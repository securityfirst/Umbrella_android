package org.secfirst.umbrella.feature.tent.interactor

import org.secfirst.umbrella.feature.base.interactor.BaseInteractor
import java.io.File

interface TentBaseInteractor : BaseInteractor {

    suspend fun updateRepository(): List<Pair<String, File>>

    suspend fun fetchRepository(url: String): Boolean

    suspend fun loadElementsFile(path : String): List<File>

}