package org.secfirst.umbrella.whitelabel.data.database.login

import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginDao: LoginDao) : LoginRepo {

    override suspend fun loginDatabase(userToken: String) = loginDao.initDatabase(userToken)

    override suspend fun changeToken(userToken: String) = loginDao.changeDatabaseAccess(userToken)
}