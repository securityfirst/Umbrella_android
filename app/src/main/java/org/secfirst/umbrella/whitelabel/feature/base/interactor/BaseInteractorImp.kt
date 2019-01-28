package org.secfirst.umbrella.whitelabel.feature.base.interactor


import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper

open class BaseInteractorImp() : BaseInteractor {

    protected lateinit var apiHelper: ApiHelper
    protected lateinit var preferenceHelper: AppPreferenceHelper
    protected lateinit var contentRepo: ContentRepo

    constructor(apiHelper: ApiHelper, preferenceHelper: AppPreferenceHelper, contentRepo: ContentRepo) : this() {
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

    override fun setSkipPassword(isSkip: Boolean) = preferenceHelper.setSkipPassword(isSkip)

    override fun isSkippPassword(): Boolean = preferenceHelper.getSkipPassword()

    override fun enablePasswordBanner(enableBanner: Boolean) = preferenceHelper.enablePasswordBanner(enableBanner)

    override fun isUserLoggedIn() = preferenceHelper.isLoggedIn()

    override fun setLoggedIn(isLogged: Boolean) = preferenceHelper.setLoggedIn(isLogged)
}
