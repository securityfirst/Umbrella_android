package org.secfirst.umbrella.whitelabel.feature.account.presenter

import com.raizlabs.android.dbflow.config.FlowManager
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase.EXTENSION
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase.NAME
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
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

    override fun submitCleanDatabase() {
        launchSilent(uiContext) {
            interactor?.resetContent()
        }
    }

    override fun submitIsLogged() {
        val res = interactor?.isUserLoggedIn() ?: false
        getView()?.isUserLogged(res)
    }

    override fun setMaskApp(value: Boolean) {
        interactor?.setMaskApp(true)
    }

    override fun submitSkippPassword() {
        val res = interactor?.isSkippPassword() ?: false
        getView()?.getSkipPassword(res)
    }

    override fun setSkipPassword(value: Boolean) {
        interactor?.setSkipPassword(value)
    }

    override fun setUserLogIn() {
        interactor?.setLoggedIn()
    }

    override fun submitChangeDatabaseAccess(userToken: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val res = it.changeDatabaseAccess(userToken)
                getView()?.isTokenChanged(res)
            }
        }
    }

    override fun submitExportDatabase(destinationDir: String, fileName: String, isWipeData: Boolean) {
        val dstDatabase = File("$destinationDir/$fileName.$EXTENSION")
        val databaseFile = FlowManager.getContext().getDatabasePath("$NAME.$EXTENSION")
        databaseFile.copyTo(dstDatabase, true)
        if (isWipeData)
            launchSilent(uiContext) { interactor?.resetContent() }

        getView()?.exportDatabaseSuccessfully()
    }

    override fun prepareShareContent(fileName: String) {
        val databaseFile = FlowManager.getContext().getDatabasePath("$NAME.$EXTENSION")
        val backupFile = File("${FlowManager.getContext().cacheDir}/$fileName.$EXTENSION")
        databaseFile.copyTo(backupFile, true)
        getView()?.onShareContent(backupFile)
    }

    override fun validateBackupPath(backupPath: String) {
        val backupDatabase = File(backupPath)
        val databaseFile = FlowManager.getContext().getDatabasePath("$NAME.$EXTENSION")
        backupDatabase.copyTo(databaseFile, true)
        if (backupDatabase.extension == EXTENSION) {
            getView()?.onImportBackupSuccess()
        } else
            getView()?.onImportBackupFail()
    }

    override fun submitInsertFeedSource(feedSources: List<FeedSource>) {
        launchSilent(uiContext) {
            interactor?.insertAllFeedSources(feedSources)
        }
    }

    override fun submitFeedLocation(feedLocation: FeedLocation) {
        launchSilent(uiContext) {
            interactor?.insertFeedLocation(feedLocation)
        }
    }

    override fun prepareView() {
        launchSilent(uiContext) {
            interactor?.let {
                val feedLocation = it.fetchFeedLocation()
                val refreshFeedInterval = it.fetchRefreshInterval()
                val feedSource = it.fetchFeedSources()
                getView()?.loadDefaultValue(feedLocation, refreshFeedInterval, feedSource)
            }
        }
    }

    override fun submitPutRefreshInterval(position: Int) {
        launchSilent(uiContext) {
            interactor?.putRefreshInterval(position)
        }
    }
}