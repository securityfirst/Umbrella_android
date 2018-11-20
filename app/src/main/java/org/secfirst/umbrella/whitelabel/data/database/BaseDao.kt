package org.secfirst.umbrella.whitelabel.data.database

import android.util.Log
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import org.secfirst.umbrella.whitelabel.UmbrellaApplication

interface BaseDao {

    fun initDatabase(userToken: String) {

        val dbConfig = FlowConfig.Builder(UmbrellaApplication.instance)
                .addDatabaseConfig(DatabaseConfig
                        .Builder(AppDatabase::class.java)
                        .databaseName(AppDatabase.NAME)
                        .openHelper { databaseDefinition, helperListener ->
                            SQLCipherHelperImpl(databaseDefinition, helperListener, userToken)
                        }.build())
                .build()
        FlowManager.init(dbConfig)
    }

    fun changeDatabaseAccess(userToken: String): Boolean {
        return try {
            FlowManager.getWritableDatabase(AppDatabase.NAME).execSQL("PRAGMA rekey = '$userToken';")
            true
        } catch (exception: Exception) {
            Log.e("test", "Error when try to change the password. ${exception.message}")
            false
        }
    }

    fun isDatabaseOK() = FlowManager.getDatabase(AppDatabase.NAME).isDatabaseIntegrityOk

}