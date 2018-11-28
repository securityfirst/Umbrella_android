package org.secfirst.umbrella.whitelabel.data.database

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

interface BaseDao {

    fun initDatabase(userToken: String) {
//
//        val dbConfig = FlowConfig.Builder(UmbrellaApplication.instance)
//                .addDatabaseConfig(DatabaseConfig
//                        .Builder(AppDatabase::class.java)
//                        .databaseName(AppDatabase.NAME)
//                        .openHelper { databaseDefinition, helperListener ->
//                            SQLCipherHelperImpl(databaseDefinition, helperListener, userToken)
//                        }.build())
//                .build()
//        FlowManager.init(dbConfig)
    }

    fun changeDatabaseAccess(userToken: String): Boolean {
//        return try {
//            FlowManager.getWritableDatabase(AppDatabase.NAME).execSQL("PRAGMA rekey = '$userToken';")
//            true
//        } catch (exception: Exception) {
//            Log.e("test", "Error when try to change the password. ${exception.message}")
//            false
//        }
        return true
    }

    fun isDatabaseOK() = FlowManager.getDatabase(AppDatabase.NAME).isDatabaseIntegrityOk

    suspend fun saveModules(modules: List<Module>) {
        withContext(ioContext) { modelAdapter<Module>().saveAll(modules) }
    }

    suspend fun saveSubjects(subjects: List<Subject>) {
        withContext(ioContext) { modelAdapter<Subject>().saveAll(subjects) }
    }

    suspend fun saveDifficulties(difficulties: List<Difficulty>) {
        withContext(ioContext) { modelAdapter<Difficulty>().saveAll(difficulties) }
    }

    suspend fun saveMarkdowns(markdowns: List<Markdown>) {
        withContext(ioContext) { modelAdapter<Markdown>().saveAll(markdowns) }
    }

    suspend fun saveChecklists(checklists: List<Checklist>) {
        withContext(ioContext) { modelAdapter<Checklist>().saveAll(checklists) }
    }

    suspend fun saveForms(forms: List<Form>) {
        withContext(ioContext) { modelAdapter<Form>().saveAll(forms) }
    }
}