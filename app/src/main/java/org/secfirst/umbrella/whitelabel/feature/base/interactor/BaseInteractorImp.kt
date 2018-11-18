package org.secfirst.umbrella.whitelabel.feature.base.interactor

import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper

open class BaseInteractorImp() : BaseInteractor {

    protected lateinit var apiHelper: ApiHelper
    protected lateinit var preferenceHelper: AppPreferenceHelper

    constructor(apiHelper: ApiHelper, preferenceHelper: AppPreferenceHelper) : this() {
        this.apiHelper = apiHelper
        this.preferenceHelper = preferenceHelper
    }

    constructor(preferenceHelper: AppPreferenceHelper) : this() {
        this.preferenceHelper = preferenceHelper
    }

}
