package org.secfirst.umbrella.whitelabel.feature.tent.interactor

import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor
import java.io.File

interface TentBaseInteractor : BaseInteractor {

    suspend fun updateRepository(): List<Pair<String, File>>

    suspend fun fetchRepository(): Boolean

    suspend fun loadElementsFile(): List<Pair<String, File>>

    fun loadFile(): List<Pair<String, File>>
}