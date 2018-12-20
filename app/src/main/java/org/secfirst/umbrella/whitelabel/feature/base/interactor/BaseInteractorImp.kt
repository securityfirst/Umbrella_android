package org.secfirst.umbrella.whitelabel.feature.base.interactor

import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

open class BaseInteractorImp() : BaseInteractor {

    protected lateinit var apiHelper: ApiHelper
    protected lateinit var preferenceHelper: AppPreferenceHelper
    protected lateinit var contentRepo: ContentRepo

    constructor(apiHelper: ApiHelper, preferenceHelper: AppPreferenceHelper, contentRepo: ContentRepo) : this() {
        this.apiHelper = apiHelper
        this.preferenceHelper = preferenceHelper
        this.contentRepo = contentRepo
    }

    override suspend fun resetContent() {
        withContext(ioContext) {
            preferenceHelper.cleanPreferences()
            contentRepo.resetContent()
        }
    }

    override fun isUserLoggedIn() = preferenceHelper.isLoggedIn()
}
