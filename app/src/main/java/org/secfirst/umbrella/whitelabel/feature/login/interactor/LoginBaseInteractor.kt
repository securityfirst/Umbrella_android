package org.secfirst.umbrella.whitelabel.feature.login.interactor

import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface LoginBaseInteractor : BaseInteractor {

    suspend fun changeDatabaseAccess(userToken: String): Boolean

    suspend fun dispatchLoginDatabaseAccess(userToken: String): Boolean
}