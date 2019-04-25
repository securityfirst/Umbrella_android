package org.secfirst.umbrella.feature.account.presenter

import com.raizlabs.android.dbflow.config.FlowManager
import org.secfirst.umbrella.data.database.AppDatabase.EXTENSION
import org.secfirst.umbrella.data.database.AppDatabase.NAME
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.disk.validateRepository
import org.secfirst.umbrella.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.feature.account.view.AccountView
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.launchSilent
import java.io.File
import javax.inject.Inject

class AccountPresenterImp<V : AccountView, I : AccountBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), AccountBasePresenter<V, I> {


    override  fun changeContentLanguage(path: String) {
        launchSilent (uiContext) {
            interactor?.let {
                if (it.serializeNewContent(path)) {
                    getView()?.onChangedLanguageSuccess()
                } else {
                    getView()?.onChangedLanguageFail()
                }
            }
        }
    }

    override fun submitIsMaskApp() {
        interactor?.let {
            getView()?.getMaskApp(it.isMaskApp())
        }
    }

    override fun setDefaultLanguage(isoCountry: String) {
        interactor?.setDefaultLanguage(isoCountry)
    }

    override fun submitDefaultLanguage() {
        interactor?.let {
            getView()?.getDefaultLanguage(it.getDefaultLanguage())
        }
    }

    override fun submitFakeView(isShowFakeView: Boolean) {
        interactor?.setFakeView(isShowFakeView)
    }

    override fun switchServerProcess(repoUrl: String) {
        launchSilent(uiContext) {
            var isReset = false
            if (validateRepository(repoUrl))
                isReset = interactor?.resetContent() ?: false

            getView()?.onSwitchServer(isReset)
        }
    }

    override fun submitExportDatabase(destinationDir: String, fileName: String, isWipeData: Boolean) {
        if (destinationDir.isNotBlank()) {
            val dstDatabase = File("$destinationDir/$fileName.$EXTENSION")
            val databaseFile = FlowManager.getContext().getDatabasePath("$NAME.$EXTENSION")
            databaseFile.copyTo(dstDatabase, true)
            if (isWipeData)
                launchSilent(uiContext) { interactor?.resetContent() }

            getView()?.exportDatabaseSuccessfully()
        }
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

    override fun submitCleanDatabase() {
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.onResetContent(it.resetContent())
            }
        }
    }

    override fun submitIsLogged() {
        val res = interactor?.isUserLoggedIn() ?: false
        getView()?.isUserLogged(res)
    }

    override fun setMaskApp(value: Boolean) {
        interactor?.setMaskApp(value)
    }

    override fun submitSkippPassword() {
        val res = interactor?.isSkippPassword() ?: false
        getView()?.getSkipPassword(res)
    }

    override fun submitInsertFeedSource(feedSources: List<FeedSource>) {
        launchSilent(uiContext) { interactor?.insertAllFeedSources(feedSources) }
    }

    override fun submitFeedLocation(feedLocation: FeedLocation) {
        launchSilent(uiContext) { interactor?.insertFeedLocation(feedLocation) }
    }

    override fun setUserLogIn() {
        interactor?.setLoggedIn(true)
    }

    override fun submitPutRefreshInterval(position: Int) {
        launchSilent(uiContext) { interactor?.putRefreshInterval(position) }
    }


    override fun setSkipPassword(value: Boolean) {
        launchSilent(uiContext) {
            interactor?.let {
                it.setSkipPassword(value)
                if (value) {
                    it.enablePasswordBanner(false)
                } else {
                    it.enablePasswordBanner(true)
                }
            }
            getView()?.getSkipPassword(value)
        }
    }

    override fun submitChangeDatabaseAccess(userToken: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val res = it.changeDatabaseAccess(userToken)
                if (res) {
                    it.setLoggedIn(true)
                    it.setSkipPassword(true)
                    it.enablePasswordBanner(false)
                }
                getView()?.isTokenChanged(res)
            }
        }
    }
}