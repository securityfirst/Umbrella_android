package org.secfirst.umbrella.whitelabel.feature.maskapp.interactor

import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor


interface MaskAppBaseInteractor : BaseInteractor {

    fun setMaskApp(res : Boolean) : Boolean
}