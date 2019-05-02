package org.secfirst.umbrella.feature.account.view


import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bluelinelabs.conductor.RouterTransaction
import com.codekidlabs.storagechooser.StorageChooser
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.account_language_dialog.view.*
import kotlinx.android.synthetic.main.account_settings_view.*
import kotlinx.android.synthetic.main.account_settings_view.view.*
import kotlinx.android.synthetic.main.account_switch_server_view.view.*
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.settings_export_dialog.view.*
import kotlinx.android.synthetic.main.tour_view.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import org.secfirst.umbrella.BuildConfig
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.DialogManager
import org.secfirst.umbrella.component.RefreshIntervalDialog
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.disk.ElementSerializeMonitor
import org.secfirst.umbrella.data.disk.IsoCountry
import org.secfirst.umbrella.data.disk.baseUrlRepository
import org.secfirst.umbrella.data.disk.getPathRepository
import org.secfirst.umbrella.feature.account.DaggerAccountComponent
import org.secfirst.umbrella.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.feature.account.presenter.AccountBasePresenter
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.content.ContentService
import org.secfirst.umbrella.feature.content.ContentView
import org.secfirst.umbrella.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.feature.content.presenter.ContentBasePresenter
import org.secfirst.umbrella.feature.reader.view.feed.FeedLocationDialog
import org.secfirst.umbrella.feature.reader.view.feed.FeedSourceDialog
import org.secfirst.umbrella.feature.tent.TentView
import org.secfirst.umbrella.feature.tent.interactor.TentBaseInteractor
import org.secfirst.umbrella.feature.tent.presenter.TentBasePresenter
import org.secfirst.umbrella.feature.tour.view.TourController
import org.secfirst.umbrella.misc.*
import java.io.File
import javax.inject.Inject


class SettingsController : BaseController(),
        AccountView,
        ContentView,
        TentView,
        FeedLocationDialog.FeedLocationListener,
        RefreshIntervalDialog.RefreshIntervalListener,
        FeedSourceDialog.FeedSourceListener,
        ElementSerializeMonitor {

    @Inject
    internal lateinit var presenter: AccountBasePresenter<AccountView, AccountBaseInteractor>
    @Inject
    internal lateinit var presentContent: ContentBasePresenter<ContentView, ContentBaseInteractor>
    @Inject
    internal lateinit var presentTent: TentBasePresenter<TentView, TentBaseInteractor>

    private var isShare = false
    private lateinit var intentService: Intent
    private lateinit var exportAlertDialog: AlertDialog
    private lateinit var switchServerDialog: AlertDialog
    private lateinit var switchServerProgress: ProgressDialog
    private lateinit var exportView: View
    private lateinit var switchServerView: View
    private lateinit var switchLanguageAlertDialog: AlertDialog
    private lateinit var switchLanguageAlertView : View
    private var isWipeData: Boolean = false
    private lateinit var mainView: View
    private lateinit var feedLocationDialog: FeedLocationDialog
    private lateinit var refreshIntervalView: View
    private lateinit var languageDialog: AlertDialog
    private lateinit var languageView: View
    private lateinit var refreshIntervalDialog: RefreshIntervalDialog
    private lateinit var feedSourceDialog: FeedSourceDialog
    private lateinit var refreshServerProgress: ProgressDialog

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val percentage = intent.getIntExtra(ContentService.EXTRA_CONTENT_SERVICE_PROGRESS, -1)
            val title = intent.getStringExtra(ContentService.EXTRA_CONTENT_SERVICE_TITLE_PROGRESS)
                    ?: ""
            val isCompleted = intent.getBooleanExtra(ContentService.ACTION_COMPLETED_FOREGROUND_SERVICE, false)
            val lostConnection = intent.getBooleanExtra(ContentService.ACTION_LOST_CONNECTION, false)

            if (title.isNotEmpty())
                switchServerDialog.setTitle(title)

            Handler().post {
                switchServerProgress.progress = 0
                switchServerProgress.incrementProgressBy(percentage)
            }

            if (isCompleted)
                contentCompleted()

            if (lostConnection)
                errorLostConnectionMessage()
        }
    }

    override fun onInject() {
        DaggerAccountComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
        enableNavigation(false)
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mMessageReceiver, IntentFilter(ContentService.EXTRA_CONTENT_SERVICE_ID))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        intentService = Intent(context, ContentService::class.java)
        mainView = inflater.inflate(R.layout.account_settings_view, container, false)
        exportView = inflater.inflate(R.layout.settings_export_dialog, container, false)
        switchServerView = inflater.inflate(R.layout.account_switch_server_view, container, false)
        languageView = inflater.inflate(R.layout.account_language_dialog, container, false)
        val feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)
        switchLanguageAlertView = inflater.inflate(R.layout.switch_language_alert, container, false)



        presenter.onAttach(this)
        presentContent.onAttach(this)
        presentTent.onAttach(this)

        exportAlertDialog = AlertDialog
                .Builder(activity)
                .setView(exportView)
                .create()

        languageDialog = AlertDialog
                .Builder(activity)
                .setView(languageView)
                .create()

        switchServerDialog = AlertDialog
                .Builder(activity)
                .setView(switchServerView)
                .setTitle(context.getString(R.string.switch_server_title_message))
                .create()

        switchLanguageAlertDialog = AlertDialog
                .Builder(activity)
                .setView(switchLanguageAlertView)
                .create()

        exportView.exportDialogWipeData.setOnClickListener { wipeDataClick() }
        exportView.alertControlOk.onClick { exportDataOk() }
        exportView.alertControlCancel.onClick { exportDataClose() }
        switchServerView.alertControlOk.onClick { switchServerOk() }
        switchServerView.alertControlCancel.onClick { switchServerDialog.dismiss() }
        languageView.alertControlOk.onClick { switchLanguageAlert() }
        languageView.alertControlCancel.onClick { languageDialog.dismiss() }
        switchLanguageAlertView.alertControlOk.onClick{changeLanguageOk()}
        switchLanguageAlertView.alertControlCancel.onClick { switchLanguageAlertDialog.dismiss() }

        mainView.settingsLanguage.onClick { languageClick() }
        mainView.settingsImportData.onClick { importDataClick() }
        mainView.settingsExportData.onClick { exportDataClick() }
        mainView.settingsLocation.onClick { setLocationClick() }
        mainView.settingsRefreshFeeds.onClick { refreshIntervalClick() }
        mainView.settingsSecurityFeed.onClick { feedSourceClick() }
        mainView.settingsRefreshServer.onClick { refreshServerClick() }
        mainView.settingsSwitchServer.onClick { switchServerClick() }
        mainView.settingsSkipPassword.setOnCheckedChangeListener { _, isChecked ->
            skipPasswordTip(isChecked)
        }
        presenter.submitDefaultLanguage()
        presenter.submitSkippPassword()
        presenter.prepareView()
        initExportGroup()
        feedLocationDialog = FeedLocationDialog(feedLocationView, this)

        initProgress()
        refreshServerProgress = ProgressDialog(context)
        refreshServerProgress.setCancelable(false)
        refreshServerProgress.setMessage(context.getString(R.string.loading_tour_message))
        return mainView
    }

    private fun initProgress() {
        switchServerProgress = ProgressDialog(context)
        switchServerProgress.setCancelable(false)
        switchServerProgress.max = 100
        switchServerProgress.setProgressStyle(R.style.ProgressDialogStyle)
        switchServerProgress.progress = 0
        switchServerProgress.setTitle(context.getString(R.string.notification_fetching_data))
        switchServerProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
    }

    private fun languageClick() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return languageDialog
            }
        })
    }

    private fun changeLanguageOk() {
        switchServerProgress.setTitle(context.getString(R.string.notification_update_database))
        when {
            languageView.spanishCheck.isChecked -> {
                context.setLocale(IsoCountry.SPANISH.value)
                presenter.setDefaultLanguage(IsoCountry.SPANISH.value)
                mainView.imageLanguage.background = ContextCompat.getDrawable(appContext(), R.drawable.es)
                presenter.changeContentLanguage("${getPathRepository()}es")
                refreshServerProgress.show()
            }
            languageView.chineseCheck.isChecked -> {
                context.setLocale(IsoCountry.CHINESE.value)
                presenter.setDefaultLanguage(IsoCountry.CHINESE.value)
                mainView.imageLanguage.background = ContextCompat.getDrawable(appContext(), R.drawable.ta)
                presenter.changeContentLanguage("${getPathRepository()}zh-Hant")
                refreshServerProgress.show()
            }
            else -> {
                context.setLocale(IsoCountry.ENGLISH.value)
                presenter.setDefaultLanguage(IsoCountry.ENGLISH.value)
                mainView.imageLanguage.background = ContextCompat.getDrawable(appContext(), R.drawable.gb)
                presenter.changeContentLanguage("${getPathRepository()}en")
                refreshServerProgress.show()
            }

        }
        languageDialog.dismiss()
    }

    override fun getDefaultLanguage(isoCountry: String) {

        when (isoCountry) {
            IsoCountry.ENGLISH.value -> {
                languageView.englishCheck.isChecked = true
                mainView.titleLanguage.text = context.getText(R.string.english_language_title)
                mainView.imageLanguage.background = ContextCompat.getDrawable(appContext(), R.drawable.gb)
            }
            IsoCountry.CHINESE.value -> {
                languageView.chineseCheck.isChecked = true
                mainView.titleLanguage.text = context.getText(R.string.chinese_language_title)
                mainView.imageLanguage.background = ContextCompat.getDrawable(appContext(), R.drawable.ta)
            }
            IsoCountry.SPANISH.value -> {
                languageView.spanishCheck.isChecked = true
                mainView.titleLanguage.text = context.getText(R.string.spanish_language_title)
                mainView.imageLanguage.background = ContextCompat.getDrawable(appContext(), R.drawable.es)
            }
            else -> {
                languageView.englishCheck.isChecked = true
                mainView.titleLanguage.text = context.getText(R.string.english_language_title)
                mainView.imageLanguage.background = ContextCompat.getDrawable(appContext(), R.drawable.gb)
            }
        }
    }

    private fun switchServerClick() = switchServerDialog.show()

    private fun refreshServerClick() {
        refreshServerProgress.show()
        mainActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        presentTent.submitUpdateRepository()
        mainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun skipPasswordTip(isChecked: Boolean) {
        if (isChecked) presenter.setSkipPassword(true) else presenter.setSkipPassword(false)
    }

    private fun feedSourceClick() = feedSourceDialog.show()

    private fun refreshIntervalClick() = refreshIntervalDialog.show()

    private fun setLocationClick() = feedLocationDialog.show()

    private fun importDataClick() {
        val chooser = StorageChooser.Builder()
                .withActivity(mainActivity)
                .withFragmentManager(mainActivity.fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build()
        chooser.show()
        chooser.setOnSelectListener { path -> presenter.validateBackupPath(path) }
    }

    private fun switchServerOk() {
        val repoUrl = switchServerView.editServer.text.toString()
        presenter.switchServerProcess(repoUrl)
    }

    private fun exportDataClick() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return exportAlertDialog
            }
        })
    }

    private fun initExportGroup() {
        exportView.exportDialogGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.exportDialogTypeExport -> isShare = false
                R.id.ExportDialogShareType -> isShare = true
            }
        }
    }

    override fun onShareContent(backupFile: File) {
        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, backupFile)
        val shareIntent = ShareCompat.IntentBuilder.from(mainActivity)
                .setType(context.contentResolver.getType(uri))
                .setStream(uri)
                .intent

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val pm = context.packageManager
        if (shareIntent.resolveActivity(pm) != null)
            startActivity(Intent.createChooser(shareIntent, context.getString(R.string.settings_umbrella_share_title)))
        exportAlertDialog.dismiss()
    }

    private fun showFileChooserPreview() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Handler().postDelayed({
                chooseFolderDialog()
            }, 500)
        } else {
            mainActivity.requestExternalStoragePermission()
        }
    }

    private fun chooseFolderDialog() {
        val chooser = StorageChooser.Builder()
                .withActivity(mainActivity)
                .withFragmentManager(mainActivity.fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build()
        chooser.show()
        chooser.setOnSelectListener { path ->
            presenter.submitExportDatabase(path, getFilename(), isWipeData)
            exportAlertDialog.dismiss()
        }
    }

    private fun checkPermission() {
        Permissions.check(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, null, object : PermissionHandler() {
            override fun onGranted() {
                showFileChooserPreview()
            }
        })
    }

    override fun loadDefaultValue(feedLocation: FeedLocation?, refreshFeedInterval: Int
                                  , feedSource: List<FeedSource>) {

        mainView.settingsLabelLocation.text = feedLocation?.location
                ?: context.getText(R.string.settings_your_location)
        refreshIntervalDialog = RefreshIntervalDialog(refreshIntervalView, refreshFeedInterval.toString(), this)
        mainView.settingsLabelRefreshInterval.text = context.getString(R.string.feed_text_min, refreshFeedInterval.toString())
        feedSourceDialog = FeedSourceDialog(feedSource, context, this)
        prepareFeedSource(feedSource)
    }

    override fun onLocationSuccess(feedLocation: FeedLocation) {
        mainView.settingsLabelLocation.text = feedLocation.location
        presenter.submitFeedLocation(feedLocation)
    }

    private fun prepareFeedSource(feedSources: List<FeedSource>) {
        if (!feedSources.any { it.lastChecked })
            mainView.settingsLabelFeedSource.text = context.getText(R.string.settings_security_feed_sources)

        val checkedSources = mutableListOf<String>()
        feedSources.filter { it.lastChecked }.forEach { checkedSources.add(it.name) }

        if (checkedSources.joinToString(" , ").isNotBlank())
            mainView.settingsLabelFeedSource.text = checkedSources.joinToString(" , ")
    }

    private fun setUpToolbar() {
        settingsToolbar?.let {
            it.title = context.getString(R.string.settings_title)
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun exportDatabaseSuccessfully() {
        context.toast(context.getString(R.string.export_database_success))
        exportAlertDialog.dismiss()
        if (isWipeData) router.pushController(RouterTransaction.with(TourController()))
    }

    override fun onRefreshIntervalSuccess(selectedInterval: String) {
        if (selectedInterval.isNotEmpty()) {
            presenter.submitPutRefreshInterval(selectedInterval.toInt())
            mainView.settingsLabelRefreshInterval.text = context.getString(R.string.feed_text_min, selectedInterval)
        }
    }

    override fun onFeedSourceSuccess(feedSources: List<FeedSource>) {
        presenter.submitInsertFeedSource(feedSources)
        prepareFeedSource(feedSources)
    }

    override fun onImportBackupFail() {
        context.toast(context.getString(R.string.import_dialog_fail_message))
    }

    override fun onImportBackupSuccess() = doRestartApplication(context)

    override fun downloadContentCompleted(res: Boolean) {
        if (res) context.toast("Updated with success.")
        refreshServerProgress.dismiss()
    }

    override fun downloadContentInProgress() {
        val dialog = DialogManager(this)
        dialog.showDialog(DialogManager.PROGRESS_DIALOG_TAG, object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                refreshServerProgress = ProgressDialog(context)
                refreshServerProgress.setCancelable(false)
                refreshServerProgress.setMessage(context?.getString(R.string.loading_tour_message))
                return refreshServerProgress
            }
        })
    }

    override fun onCleanDatabaseSuccess() = presentContent.manageContent(baseUrlRepository)

    private fun exportDataOk() {
        if (isShare)
            presenter.prepareShareContent(getFilename())
        else
            checkPermission()
    }

    private fun exportDataClose() = exportAlertDialog.dismiss()


    private fun wipeDataClick() {
        isWipeData = true
    }

    override fun getSkipPassword(res: Boolean) {
        mainView.settingsSkipPassword.isChecked = res
    }

    private fun getFilename(): String {
        val fileName = exportView.ExportDialogFileName.text.toString()
        return if (fileName.isBlank()) context.getString(R.string.export_dialog_default_message) else fileName
    }

    override fun isUpdateRepository(pairFiles: List<Pair<String, File>>) {
        context.toast(context.getString(R.string.update_repository_message_success))
        presentContent.updateContent(pairFiles)
        refreshServerProgress.dismiss()
    }

    override fun onSwitchServer(isSwitch: Boolean) {
        val newContentUrl = switchServerView.editServer.text.toString()
        if (isSwitch) {
            switchServerDialog.dismiss()
            intentService.apply {
                putExtra(ContentService.EXTRA_URL_REPOSITORY, newContentUrl)
                action = ContentService.ACTION_START_FOREGROUND_SERVICE
            }
            if (context.isInternetConnected()) {
                mainActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                context.startService(intentService)
                switchServerProgress.show()
            } else
                tourView?.apply { errorNoConnectionMessage(this) }
        } else
            context.longToast(context.getString(R.string.switch_server_error_message))
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(mMessageReceiver)
        mainActivity.stopService(intentService)
        switchServerDialog.dismiss()
    }

    override fun onDestroyView(view: View) {
        mainActivity.stopService(intentService)
        super.onDestroyView(view)
    }

    override fun onResetContent(res: Boolean) {
        if (res) doRestartApplication(context)
    }

    private fun errorNoConnectionMessage(view: View) {
        val snackBar = view.longSnackbar(context.resources.getString(R.string.error_connection_tour_message))
        val snackView = snackBar.view
        snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun contentCompleted() {
        switchServerProgress.dismiss()
        mainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun errorLostConnectionMessage() {
        tourView?.let {
            val snackBar = it.longSnackbar(context.resources.getString(R.string.notification_lost_connection))
            val snackView = snackBar.view
            snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        switchServerDialog.dismiss()
    }

    override fun onChangedLanguageSuccess() {
        refreshServerProgress.dismiss()
        mainActivity.recreate()
        mainActivity.navigationPositionToCenter()
    }

    override fun onChangedLanguageFail() {
        context.toast(context.getString(R.string.change_language_error))
        refreshServerProgress.dismiss()
    }

    override fun onSerializeProgress(percentage: Int) {
        refreshServerProgress.incrementProgressBy(percentage)
    }

    private fun switchLanguageAlert() {
        switchLanguageAlertDialog.show()
        languageDialog.dismiss()
    }
}
