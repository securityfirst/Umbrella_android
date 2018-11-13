package org.secfirst.umbrella.whitelabel.data.database.login

interface LoginRepo {

    suspend fun loginDatabase(userToken: String): Boolean

    suspend fun changeToken(userToken: String): Boolean
}