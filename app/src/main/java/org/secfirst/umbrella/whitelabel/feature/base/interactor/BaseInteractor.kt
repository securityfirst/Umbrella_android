package org.secfirst.umbrella.whitelabel.feature.base.interactor

interface BaseInteractor {

    fun isUserLoggedIn(): Boolean

    fun setLoggedIn(isLogged: Boolean): Boolean

    fun enablePasswordBanner(enableBanner: Boolean): Boolean

    fun setSkipPassword(isSkip: Boolean): Boolean

    fun isSkippPassword(): Boolean

    suspend fun resetContent(): Boolean
}
