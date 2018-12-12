package org.secfirst.umbrella.whitelabel.feature.login.presenter

import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteException
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.login.view.LoginView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject


class LoginPresenterImp<V : LoginView, I : LoginBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LoginBasePresenter<V, I> {

    override fun submitChangeDatabaseAccess(userToken: String) {
        interactor?.let {
            launchSilent(uiContext) {
                if (testLogin(userToken)) {
                    it.dispatchLoginDatabaseAccess(userToken)
                    getView()?.isLoginOk(true)
                } else {
                    getView()?.isLoginOk(false)
                }
            }
        }

    }

    fun testLogin(userToken: String): Boolean {
        SQLiteDatabase.loadLibs(UmbrellaApplication.instance)
        try {
            val db = SQLiteDatabase.openOrCreateDatabase(UmbrellaApplication.instance.getDatabasePath(AppDatabase.NAME+".db").getPath(), userToken, null)
            return db.isOpen
        } catch (e : SQLiteException) {
            e.printStackTrace()
            return false
        }
    }
}