package org.secfirst.umbrella.feature.login.presenter

import net.sqlcipher.database.SQLiteDatabase
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.feature.login.view.LoginView
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.launchSilent
import javax.inject.Inject


class LoginPresenterImp<V : LoginView, I : LoginBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LoginBasePresenter<V, I> {

    override fun submitResetPassword() {
        launchSilent(uiContext) {
            interactor?.let {
                val res = it.resetContent()
                getView()?.onResetContent(res)
            }
        }
    }

    override fun submitChangeDatabaseAccess(userToken: String) {
        interactor?.let {
            launchSilent(uiContext) {
                if (checkPassword(userToken)) {
                    it.dispatchLoginDatabaseAccess(userToken)
                    it.setSkipPassword(true)
                    getView()?.isLoginOk(true)
                } else {
                    getView()?.isLoginOk(false)
                }
            }
        }
    }

    private fun checkPassword(userToken: String): Boolean {
        val application = UmbrellaApplication.instance
        SQLiteDatabase.loadLibs(application)
        return try {
            val db = SQLiteDatabase.openOrCreateDatabase(application
                    .getDatabasePath(AppDatabase.NAME + ".db").path, userToken, null)
            db.isOpen
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}