package org.secfirst.umbrella.whitelabel.feature.login.interactor

import org.secfirst.umbrella.whitelabel.data.database.login.LoginRepo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LoginInteractorImp @Inject constructor(private val loginRepo: LoginRepo) : BaseInteractorImp(), LoginBaseInteractor {

    override suspend fun dispatchLoginDatabaseAccess(userToken: String) = loginRepo.loginDatabase(userToken)

    override suspend fun changeDatabaseAccess(userToken: String) = loginRepo.changeToken(userToken)

}