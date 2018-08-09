package org.secfirst.umbrella.whitelabel.data.database

import com.raizlabs.android.dbflow.annotation.Database

@Database(version = AppDatabase.VERSION, generatedClassSeparator = "_", foreignKeyConstraintsEnforced = true)

object AppDatabase {

    const val NAME = "AppDatabase"
    const val VERSION = 1
}




