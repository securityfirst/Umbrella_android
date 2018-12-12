package org.secfirst.umbrella.whitelabel.data.database.login

interface LoginRepo {

    fun loginDatabase(userToken: String)

    suspend fun changeToken(userToken: String): Boolean

    fun verifyDatabaseIntegrity() : Boolean
}