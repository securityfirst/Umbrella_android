package org.secfirst.umbrella.whitelabel.feature.account.presenter

import copyFilee
import org.secfirst.umbrella.whitelabel.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.account.view.AccountView
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import java.io.File
import javax.inject.Inject

class AccountPresenterImp<V : AccountView, I : AccountBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), AccountBasePresenter<V, I> {


    override fun setUserLogIn() {
        interactor?.setLoggedIn()
    }

    override fun submitChangeDatabaseAccess(userToken: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val res = it.changeDatabaseAccess(userToken)
                getView()?.isLogged(res)
            }
        }
    }

    override fun submitExportDatabase(destinationDir: String, fileName: String, databaseFile: File) {
        val dstDatabase = File("$destinationDir/$fileName.db")

        copyFilee(databaseFile, dstDatabase)
        getView()?.exportDatabaseSuccessfully()
    }
}