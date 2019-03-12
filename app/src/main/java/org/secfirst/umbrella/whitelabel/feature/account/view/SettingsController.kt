package org.secfirst.umbrella.whitelabel.feature.account.view


import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bluelinelabs.conductor.RouterTransaction
import com.codekidlabs.storagechooser.StorageChooser
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.account_language_dialog.view.*
import kotlinx.android.synthetic.main.account_settings_view.*
import kotlinx.android.synthetic.main.account_settings_view.view.*
import kotlinx.android.synthetic.main.account_switch_server_view.view.*
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*
import kotlinx.android.synthetic.main.settings_export_dialog.view.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.component.RefreshIntervalDialog
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.disk.IsoCountry
import org.secfirst.umbrella.whitelabel.data.disk.baseUrlRepository
import org.secfirst.umbrella.whitelabel.feature.account.DaggerAccountComponent
import org.secfirst.umbrella.whitelabel.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.account.presenter.AccountBasePresenter
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.content.presenter.ContentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.feed.FeedLocationDialog
import org.secfirst.umbrella.whitelabel.feature.reader.view.feed.FeedSourceDialog
import org.secfirst.umbrella.whitelabel.feature.tent.TentView
import org.secfirst.umbrella.whitelabel.feature.tent.interactor.TentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.tent.presenter.TentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
import org.secfirst.umbrella.whitelabel.misc.appContext
import org.secfirst.umbrella.whitelabel.misc.doRestartApplication
import org.secfirst.umbrella.whitelabel.misc.requestExternalStoragePermission
import org.secfirst.umbrella.whitelabel.misc.setLocale
import java.io.File
import javax.inject.Inject


class SettingsController : BaseController(), AccountView, ContentView, TentView, FeedLocationDialog.FeedLocationListener,
        RefreshIntervalDialog.RefreshIntervalListener, FeedSourceDialog.FeedSourceListener {

    @Inject
    internal lateinit var presenter: AccountBasePresenter<AccountView, AccountBaseInteractor>
    @Inject
    internal lateinit var presentContent: ContentBasePresenter<ContentView, ContentBaseInteractor>
    @Inject
    internal lateinit var presentTent: TentBasePresenter<TentView, TentBaseInteractor>

    private lateinit var exportAlertDialog: AlertDialog
    private lateinit var switchServerDialog: AlertDialog
    private lateinit var exportView: View
    private lateinit var switchServerView: View
    private var destinationPath = ""
    private var isWipeData: Boolean = false
    private lateinit var mainView: View
    private lateinit var feedLocationDialog: FeedLocationDialog
    private lateinit var refreshIntervalView: View
    private lateinit var languageDialog: AlertDialog
    private lateinit var languageView: View
    private lateinit var refreshIntervalDialog: RefreshIntervalDialog
    private lateinit var feedSourceDialog: FeedSourceDialog
    private lateinit var refreshServerProgress: ProgressDialog

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        mainView = inflater.inflate(R.layout.account_settings_view, container, false)
        exportView = inflater.inflate(R.layout.settings_export_dialog, container, false)
        switchServerView = inflater.inflate(R.layout.account_switch_server_view, container, false)
        languageView = inflater.inflate(R.layout.account_language_dialog, container, false)
        val feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)

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

        exportView.exportDialogWipeData.setOnClickListener { wipeDataClick() }
        exportView.alertControlOk.onClick { exportDataOk() }
        exportView.alertControlCancel.onClick { exportDataClose() }
        switchServerView.alertControlOk.onClick { switchServerOk() }
        switchServerView.alertControlCancel.onClick { switchServerDialog.dismiss() }
        languageView.alertControlOk.onClick { changeLanguageOk() }
        languageView.alertControlCancel.onClick { languageDialog.dismiss() }

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

        return mainView
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
        when {
            languageView.spanishCheck.isChecked -> {
                context.setLocale(IsoCountry.SPANISH.value)
                presenter.setDefaultLanguage(IsoCountry.SPANISH.value)
            }
            languageView.chineseCheck.isChecked -> {
                context.setLocale(IsoCountry.CHINESE.value)
                presenter.setDefaultLanguage(IsoCountry.CHINESE.value)
            }
            else -> {
                context.setLocale(IsoCountry.ENGLISH.value)
                presenter.setDefaultLanguage(IsoCountry.ENGLISH.value)
            }
        }
        mainActivity.recreate()
        mainActivity.navigationPositionToCenter()
        languageDialog.dismiss()
    }

    override fun getDefaultLanguage(isoCountry: String) {
        when (isoCountry) {
            IsoCountry.ENGLISH.value -> {
                languageView.englishCheck.isChecked = true
                mainView.titleLanguage.text = context.getText(R.string.english_language_title)
            }
            IsoCountry.CHINESE.value -> {
                languageView.chineseCheck.isChecked = true
                mainView.titleLanguage.text = context.getText(R.string.chinese_language_title)
            }
            IsoCountry.SPANISH.value -> {
                languageView.spanishCheck.isChecked = true
                mainView.titleLanguage.text = context.getText(R.string.spanish_language_title)
            }
        }
    }

    private fun switchServerClick() {
        val dialogManager = DialogManager(this)
        val length = switchServerView.editServer.text.toString().length
        switchServerView.editServer.setSelection(length)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return switchServerDialog
            }
        })
    }

    private fun refreshServerClick() {
        val dialog = DialogManager(this)
        dialog.showDialog(DialogManager.PROGRESS_DIALOG_TAG, object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                refreshServerProgress = ProgressDialog(context)
                refreshServerProgress.setCancelable(false)
                refreshServerProgress.setMessage(context?.getString(R.string.loading_tour_message))
                return refreshServerProgress
            }
        })
        presentTent.submitUpdateRepository()
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
                R.id.exportDialogTypeExport -> checkPermission()
                R.id.ExportDialogShareType -> presenter.prepareShareContent(getFilename())
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
    }

    private fun showFileChooserPreview() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            chooseFolderDialog()
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
            destinationPath = path
        }
    }

    private fun checkPermission() {
        Permissions.check(appContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, null, object : PermissionHandler() {
            override fun onGranted() {
                showFileChooserPreview()
            }
        })
    }

    override fun loadDefaultValue(feedLocation: FeedLocation?, refreshFeedInterval: Int
                                  , feedSource: List<FeedSource>) {

        mainView.settingsLabelLocation.text = feedLocation?.location
                ?: context.getText(R.string.settings_your_location)
        refreshIntervalDialog = RefreshIntervalDialog(refreshIntervalView, refreshFeedInterval, this)
        mainView.settingsLabelRefreshInterval.text = refreshIntervalView.refreshInterval.selectedItem.toString()
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

    override fun onRefreshIntervalSuccess(selectedPosition: Int, selectedInterval: String) {
        presenter.submitPutRefreshInterval(selectedPosition)
        mainView.settingsLabelRefreshInterval.text = refreshIntervalView.refreshInterval.selectedItem.toString()
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

    private fun exportDataOk() = presenter.submitExportDatabase(destinationPath, getFilename(), isWipeData)

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
            presentContent.manageContent(newContentUrl)
        } else
            context.longToast(context.getString(R.string.switch_server_error_message))
    }

    override fun onResetContent(res: Boolean) {
        if (res) doRestartApplication(context)
    }
}