package org.secfirst.umbrella.whitelabel.data.database

import com.raizlabs.android.dbflow.annotation.Database

@Database(version = AppDatabase.VERSION,
        generatedClassSeparator = "_",
        foreignKeyConstraintsEnforced = true)

object AppDatabase {

    const val DEFAULT = "umbrella"
    const val NAME = "umbrella_database"
    const val VERSION = 1
    const val EXTENSION = "db"
}




