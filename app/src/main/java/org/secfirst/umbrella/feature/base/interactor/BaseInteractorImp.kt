package org.secfirst.umbrella.feature.base.interactor


import org.secfirst.umbrella.data.database.content.ContentRepo
import org.secfirst.umbrella.data.network.ApiHelper
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper

open class BaseInteractorImp() : BaseInteractor {

    protected lateinit var apiHelper: ApiHelper
    protected lateinit var preferenceHelper: AppPreferenceHelper
    protected lateinit var contentRepo: ContentRepo

    constructor(
        apiHelper: ApiHelper,
        preferenceHelper: AppPreferenceHelper,
        contentRepo: ContentRepo
    ) : this() {
        this.apiHelper = apiHelper
        this.preferenceHelper = preferenceHelper
        this.contentRepo = contentRepo
    }

    override suspend fun resetContent(): Boolean {
        val res: Boolean = contentRepo.resetContent()
        preferenceHelper.setLoggedIn(false)
        preferenceHelper.setSkipPassword(false)
        preferenceHelper.setMaskApp(false)
        return res
    }

    override fun setDefaultLanguage(isoCountry: String) = preferenceHelper.setLanguage(isoCountry)

    override fun getDefaultLanguage() = preferenceHelper.getLanguage()

    override fun setDarkMode(value: Boolean) {
        preferenceHelper.setDarkMode(value)
    }

    override fun isDarkMode(): Boolean = preferenceHelper.getDarkMode()

    override fun setSkipPassword(isSkip: Boolean) = preferenceHelper.setSkipPassword(isSkip)

    override fun isSkippPassword(): Boolean = preferenceHelper.getSkipPassword()

    override fun enablePasswordBanner(enableBanner: Boolean) =
        preferenceHelper.enablePasswordBanner(enableBanner)

    override fun isUserLoggedIn() = preferenceHelper.isLoggedIn()

    override fun setLoggedIn(isLogged: Boolean) = preferenceHelper.setLoggedIn(isLogged)
}
