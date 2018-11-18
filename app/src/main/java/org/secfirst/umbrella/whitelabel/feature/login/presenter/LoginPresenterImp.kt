package org.secfirst.umbrella.whitelabel.feature.login.presenter

import android.util.Log
import com.raizlabs.android.dbflow.config.FlowManager
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
                it.dispatchLoginDatabaseAccess(userToken)
                try {
                    getView()?.isLoginOk(test())
                } catch (ex: Exception) {
                    FlowManager.close()
                    getView()?.isLoginOk(false)
                }
            }
        }
    }

    fun test(): Boolean {
        val a = FlowManager.getDatabase(AppDatabase.NAME).helper.isDatabaseIntegrityOk
        Log.i("test", "")
        return a
    }

}