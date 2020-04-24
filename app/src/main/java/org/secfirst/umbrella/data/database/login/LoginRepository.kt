package org.secfirst.umbrella.data.database.login

import javax.inject.Inject

class LoginRepository @Inject constructor(private val loginDao: LoginDao) : LoginRepo {

    override fun verifyDatabaseIntegrity() = loginDao.isDatabaseOK()

    override fun loginDatabase(userToken: String) = loginDao.initDatabase(userToken)

    override suspend fun changeToken(userToken: String) = loginDao.changeDatabaseAccess(userToken)
}