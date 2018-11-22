package org.secfirst.umbrella.whitelabel.feature.account.presenter

import com.raizlabs.android.dbflow.config.FlowManager
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
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

    override fun submitExportDatabase(destinationDir: String, fileName: String, isWipeData: Boolean) {

        val dstDatabase = File("$destinationDir/$fileName.db")
        val databaseFile = FlowManager.getContext().getDatabasePath("${AppDatabase.NAME}.db")
        databaseFile.copyTo(dstDatabase, true)
        if (isWipeData)
            cleanDatabase()

        getView()?.exportDatabaseSuccessfully()
    }

    private fun cleanDatabase() {
        val cacheDir = UmbrellaApplication.instance.cacheDir
        FileUtils.deleteQuietly(cacheDir)
        FlowManager.getDatabase(AppDatabase.NAME).reset()
    }


    override fun prepareShareContent(fileName: String) {
        val databaseFile = FlowManager.getContext().getDatabasePath("${AppDatabase.NAME}.db")
        val backupFile = File("${FlowManager.getContext().cacheDir}/$fileName.db")
        databaseFile.copyTo(backupFile, true)
        getView()?.onShareContent(backupFile)
    }

    override fun validateBackupPath(backupPath: String) {
        val backupDatabase = File(backupPath)
        val databaseFile = FlowManager.getContext().getDatabasePath("${AppDatabase.NAME}.db")
        backupDatabase.copyTo(databaseFile, true)
        if (backupDatabase.extension == AppDatabase.EXTENSION) {
            getView()?.onImportBackupSuccess()
        } else
            getView()?.onImportBackupFail()
    }

    override fun submitInsertFeedSource(feedSources: List<FeedSource>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                feedLocation?.let { safeLocation -> getView()?.
                        loadDefaultValue(safeLocation, refreshFeedInterval) }
            }
        }
    }

    override fun submitPutRefreshInterval(position: Int) {
        launchSilent(uiContext) {
            interactor?.putRefreshInterval(position)
        }
    }
}