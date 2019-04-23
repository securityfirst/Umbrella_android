package org.secfirst.umbrella.feature.maskapp.interactor

import org.secfirst.umbrella.feature.base.interactor.BaseInteractor


interface MaskAppBaseInteractor : BaseInteractor {

    fun setMaskApp(res : Boolean) : Boolean
}