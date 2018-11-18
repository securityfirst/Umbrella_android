package org.secfirst.umbrella.whitelabel.data.database.main

interface MainRepo {

    suspend fun loginDatabase(userToken: String)

}