package org.secfirst.umbrella.feature.login.interactor

import org.secfirst.umbrella.feature.base.interactor.BaseInteractor

interface LoginBaseInteractor : BaseInteractor {

    suspend fun changeDatabaseAccess(userToken: String): Boolean

    fun dispatchLoginDatabaseAccess(userToken: String)

    fun dispatchDatabaseIntegrity(): Boolean
}