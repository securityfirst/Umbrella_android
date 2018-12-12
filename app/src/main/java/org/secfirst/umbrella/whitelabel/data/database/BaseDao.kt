package org.secfirst.umbrella.whitelabel.data.database

import android.util.Log
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist_Table
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.whitelabel.data.database.form.Form
import org.secfirst.umbrella.whitelabel.data.database.form.Form_Table
import org.secfirst.umbrella.whitelabel.data.database.form.Item
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject_Table
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown_Table
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext

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

    suspend fun saveModules(modules: List<Module>) {
        withContext(ioContext) {
            modelAdapter<Module>().updateAll(modules)
        }
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
        withContext(ioContext) {
            forms.forEach { form ->
                modelAdapter<Form>().save(form)
            }
            forms.forEach { form ->
                form.screens.forEach { screen ->
                    screen.items.forEach { item ->
                        modelAdapter<Item>().save(item)
                    }
                }
            }
        }
    }

    suspend fun getModule(pathID: String) =
            withContext(ioContext) {
                SQLite.select()
                        .from(Module::class.java)
                        .where(Module_Table.id.`is`(pathID))
                        .querySingle()
            }

    suspend fun getSubject(pathID: String) =
            withContext(ioContext) {
                SQLite.select()
                        .from(Subject::class.java)
                        .where(Subject_Table.id.`is`(pathID))
                        .querySingle()
            }

    suspend fun getDifficulty(pathID: String) =
            withContext(ioContext) {
                SQLite.select()
                        .from(Difficulty::class.java)
                        .where(Difficulty_Table.id.`is`(pathID))
                        .querySingle()
            }

    suspend fun getChecklist(pathID: String) =
            withContext(ioContext) {
                SQLite.select()
                        .from(Checklist::class.java)
                        .where(Checklist_Table.id.`is`(pathID))
                        .querySingle()
            }

    suspend fun getMarkdown(pathID: String) =
            withContext(ioContext) {
                SQLite.select()
                        .from(Markdown::class.java)
                        .where(Markdown_Table.id.`is`(pathID))
                        .querySingle()
            }

    suspend fun getForm(pathID: String) =
            withContext(ioContext) {
                SQLite.select()
                        .from(Form::class.java)
                        .where(Form_Table.path.`is`(pathID))
                        .querySingle()
            }

}