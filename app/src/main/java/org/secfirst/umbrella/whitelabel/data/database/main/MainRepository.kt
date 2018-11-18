package org.secfirst.umbrella.whitelabel.data.database.main

import javax.inject.Inject

class MainRepository @Inject constructor(private val mainDao: MainDao) : MainRepo {

    override suspend fun loginDatabase(userToken: String) = mainDao.initDatabase(userToken)
}