package org.secfirst.umbrella.whitelabel.data.database.account

interface AccountRepo {

    suspend fun loginDatabase(userToken: String)

    suspend fun changeToken(userToken: String): Boolean
}