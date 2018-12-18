package org.secfirst.umbrella.whitelabel.feature.maskapp.interactor

import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class MaskAppInteractorImp @Inject constructor(preferenceHelper: AppPreferenceHelper)
    : BaseInteractorImp(preferenceHelper), MaskAppBaseInteractor {


    override fun setMaskApp(res: Boolean) = preferenceHelper.setMaskApp(res)

}