package org.secfirst.umbrella.whitelabel.data.database.form

import android.util.Log
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext


interface FormDao {

    suspend fun insertAnswer(answer: Answer) {
        withContext(ioContext) {
            try {
                modelAdapter<Answer>().insert(answer)
            } catch (e: Exception) {
                Log.e(FormDao::class.simpleName, "Error when tried to insert a answer - ${e.stackTrace}")
            }
        }
    }

    suspend fun delete(activeForm: ActiveForm) {
        withContext(ioContext) {
            try {
                modelAdapter<ActiveForm>().delete(activeForm)
            } catch (e: Exception) {
                Log.e(FormDao::class.simpleName, "Error when tried to delete a activeForm - ${e.stackTrace}")
            }
        }
    }

    suspend fun saveActiveForm(activeForm: ActiveForm): Boolean {
        var res = false
        withContext(ioContext) {
            try {
                res = modelAdapter<ActiveForm>().save(activeForm)
            } catch (e: Exception) {
                Log.e(FormDao::class.simpleName, "Error when tried to insert a activeForm - ${e.stackTrace}")
            }
        }
        return res
    }

    suspend fun getAnswerBy(formId: Long): List<Answer> = withContext(ioContext) {
        SQLite.select()
                .from(Answer::class.java)
                .where(Answer_Table.activeForm_id.`is`(formId))
                .queryList()
    }

    suspend fun getAllFormModel(): List<Form> = withContext(ioContext) {
        SQLite.select()
                .from(Form::class.java)
                .queryList()
    }

    suspend fun getAllActiveForms(): List<ActiveForm> = withContext(ioContext) {
        SQLite.select()
                .from(ActiveForm::class.java)
                .queryList()
    }

    suspend fun getScreenBy(formId: Long): List<Screen> = withContext(ioContext) {
        SQLite.select()
                .from(Screen::class.java)
                .where(Screen_Table.form_id.`is`(formId))
                .queryList()
    }
}