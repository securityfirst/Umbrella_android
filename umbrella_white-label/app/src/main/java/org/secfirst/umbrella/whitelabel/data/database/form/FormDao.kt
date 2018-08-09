package org.secfirst.umbrella.whitelabel.data.database.form

import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.*
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext


interface FormDao {

    suspend fun insertAnswer(answer: Answer) {
        withContext(ioContext) {
            modelAdapter<Answer>().insert(answer)
        }
    }

    suspend fun delete(form: Form) {
        withContext(ioContext) {
            modelAdapter<Answer>().deleteAll(form.answers)
            SQLite.delete(Form::class.java)
                    .where(Form_Table.id.eq(form.id))
                    .async()
                    .execute()
        }
    }

    suspend fun saveForm(form: Form) {
        withContext(ioContext) {
            modelAdapter<Form>().save(form)
        }
    }

    suspend fun getAnswerBy(formId: Long): List<Answer> = withContext(ioContext) {
        SQLite.select()
                .from(Answer::class.java)
                .where(Answer_Table.form_id.`is`(formId))
                .queryList()
    }

    suspend fun getAllFormModel(): List<Form> = withContext(ioContext) {
        SQLite.select()
                .from(Form::class.java)
                .where(Form_Table.active.`is`(false))
                .queryList()
    }

    suspend fun getAllActiveForms(): List<Form> = withContext(ioContext) {
        SQLite.select()
                .from(Form::class.java)
                .where(Form_Table.active.`is`(true))
                .queryList()
    }

    suspend fun getFormIdBy(title: String): Long? = withContext(ioContext) {
        val form = SQLite.select()
                .from(Form::class.java)
                .where(Form_Table.title.`is`(title))
                .querySingle()
        form?.id
    }

    suspend fun getScreenBy(formId: Long): List<Screen> = withContext(ioContext) {
        SQLite.select()
                .from(Screen::class.java)
                .where(Screen_Table.form_id.`is`(formId))
                .queryList()
    }
}