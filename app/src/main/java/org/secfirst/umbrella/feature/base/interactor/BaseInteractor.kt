package org.secfirst.umbrella.feature.base.interactor

interface BaseInteractor {

    fun isUserLoggedIn(): Boolean

    fun setLoggedIn(isLogged: Boolean): Boolean

    fun enablePasswordBanner(enableBanner: Boolean): Boolean

    fun setSkipPassword(isSkip: Boolean): Boolean

    fun setDarkMode(value: Boolean)

    fun isDarkMode(): Boolean

    fun isSkippPassword(): Boolean

    fun setDefaultLanguage(isoCountry: String): Boolean

    fun getDefaultLanguage(): String

    suspend fun resetContent(): Boolean
}
