package org.secfirst.umbrella.whitelabel.feature.base.interactor

interface BaseInteractor {

    fun isUserLoggedIn(): Boolean

    suspend fun resetContent()
}
