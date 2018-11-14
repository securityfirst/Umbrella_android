package org.secfirst.umbrella.whitelabel.data.database.account

interface AccountRepo {

    suspend fun loginDatabase(userToken: String): Boolean

    suspend fun changeToken(userToken: String): Boolean
}