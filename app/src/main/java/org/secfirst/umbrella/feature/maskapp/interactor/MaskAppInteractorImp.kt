package org.secfirst.umbrella.feature.maskapp.interactor

import org.secfirst.umbrella.data.database.content.ContentRepo
import org.secfirst.umbrella.data.network.ApiHelper
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class MaskAppInteractorImp @Inject constructor(apiHelper: ApiHelper, preferenceHelper: AppPreferenceHelper, contentRepo: ContentRepo)
    : BaseInteractorImp(apiHelper, preferenceHelper, contentRepo), MaskAppBaseInteractor {

    override fun setMaskApp(res: Boolean) = preferenceHelper.setMaskApp(res)
}