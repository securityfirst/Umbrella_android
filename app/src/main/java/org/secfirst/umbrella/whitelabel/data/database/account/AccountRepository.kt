package org.secfirst.umbrella.whitelabel.data.database.account

import javax.inject.Inject

class AccountRepository @Inject constructor(private val accountDao: AccountDao) : AccountRepo {

    override suspend fun loginDatabase(userToken: String) = accountDao.initDatabase(userToken)

    override suspend fun changeToken(userToken: String) = accountDao.changeDatabaseAccess(userToken)

}