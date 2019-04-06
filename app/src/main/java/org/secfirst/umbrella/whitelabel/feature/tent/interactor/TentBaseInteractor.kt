package org.secfirst.umbrella.whitelabel.feature.tent.interactor

import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor
import java.io.File

interface TentBaseInteractor : BaseInteractor {

    suspend fun updateRepository(): List<Pair<String, File>>

    suspend fun fetchRepository(url: String): Boolean

    suspend fun loadElementsFile(): List<File>

    fun loadFile(): List<File>
}