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
import kotlinx.android.synthetic.main.account_settings_view.*
import kotlinx.android.synthetic.main.account_settings_view.view.*
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*
import kotlinx.android.synthetic.main.settings_export_dialog.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.component.RefreshIntervalDialog
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
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
import org.secfirst.umbrella.whitelabel.misc.PERMISSION_REQUEST_EXTERNAL_STORAGE
import org.secfirst.umbrella.whitelabel.misc.doRestartApplication
import org.secfirst.umbrella.whitelabel.misc.requestExternalStoragePermission
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
    private lateinit var exportView: View
    private var destinationPath = ""
    private var isWipeData: Boolean = false
    private lateinit var mainView: View
    private lateinit var feedLocationDialog: FeedLocationDialog
    private lateinit var refreshIntervalView: View
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
        val feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)

        presenter.onAttach(this)
        presentContent.onAttach(this)
        presentTent.onAttach(this)

        exportAlertDialog = AlertDialog
                .Builder(activity)
                .setView(exportView)
                .create()

        exportView.exportDialogWipeData.setOnClickListener { wipeDataClick() }
        exportView.exportDialogOk.onClick { exportDataOk() }
        exportView.exportDialogCancel.onClick { exportDataClose() }

        mainView.settingsImportData.onClick { importDataClick() }
        mainView.settingsExportData.setOnClickListener { exportDataClick() }
        mainView.settingsLocation.setOnClickListener { setLocationClick() }
        mainView.settingsRefreshFeeds.setOnClickListener { refreshIntervalClick() }
        mainView.settingsSecurityFeed.setOnClickListener { feedSourceClick() }
        mainView.settingsRefreshServer.setOnClickListener { refreshServerClick() }
        mainView.settingsSkipPassword.setOnCheckedChangeListener { _, isChecked ->
            skipPasswordTip(isChecked)
        }
        presenter.submitSkippPassword()
        presenter.prepareView()
        initExportGroup()
        feedLocationDialog = FeedLocationDialog(feedLocationView, this)

        return mainView
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
                R.id.exportDialogTypeExport -> showFileChooserPreview()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFileChooserPreview()
            } else {
                // Permission request was denied.
            }
        }
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

    override fun onCleanDatabaseSuccess() = presentContent.manageContent()

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
}