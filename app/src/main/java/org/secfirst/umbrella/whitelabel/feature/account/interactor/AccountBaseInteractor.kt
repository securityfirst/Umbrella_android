package org.secfirst.umbrella.whitelabel.feature.account.interactor

import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface AccountBaseInteractor : BaseInteractor {

    suspend fun accessDatabase(userToken: String)

    suspend fun changeDatabaseAccess(userToken: String): Boolean

    fun setLoggedIn(): Boolean
}