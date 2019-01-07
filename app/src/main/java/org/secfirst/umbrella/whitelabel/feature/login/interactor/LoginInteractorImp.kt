package org.secfirst.umbrella.whitelabel.feature.login.interactor

import org.secfirst.umbrella.whitelabel.data.database.content.ContentRepo
import org.secfirst.umbrella.whitelabel.data.database.login.LoginRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class LoginInteractorImp @Inject constructor(private val loginRepo: LoginRepo,
                                             apiHelper: ApiHelper,
                                             preferenceHelper: AppPreferenceHelper,
                                             contentRepo: ContentRepo) : BaseInteractorImp(apiHelper, preferenceHelper, contentRepo), LoginBaseInteractor {


    override fun dispatchDatabaseIntegrity() = loginRepo.verifyDatabaseIntegrity()

    override fun dispatchLoginDatabaseAccess(userToken: String) = loginRepo.loginDatabase(userToken)

    override suspend fun changeDatabaseAccess(userToken: String) = loginRepo.changeToken(userToken)

}