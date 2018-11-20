package org.secfirst.umbrella.whitelabel.feature.account.view

import Extensions.Companion.PERMISSION_REQUEST_EXTERNAL_STORAGE
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.codekidlabs.storagechooser.StorageChooser
import kotlinx.android.synthetic.main.account_settings_view.*
import kotlinx.android.synthetic.main.account_settings_view.view.*
import kotlinx.android.synthetic.main.settings_export_dialog.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.feature.account.DaggerAccountComponent
import org.secfirst.umbrella.whitelabel.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.account.presenter.AccountBasePresenter
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
import requestExternalStoragePermission
import java.io.File
import javax.inject.Inject

class SettingsController : BaseController(), AccountView {

    private lateinit var exportAlertDialog: AlertDialog
    private lateinit var exportView: View
    private lateinit var destinationPath: String
    private var isWipeData: Boolean = false
    @Inject
    internal lateinit var presenter: AccountBasePresenter<AccountView, AccountBaseInteractor>

    override fun onInject() {
        DaggerAccountComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
        disableNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.account_settings_view, container, false)
        exportView = inflater.inflate(R.layout.settings_export_dialog, container, false)
        exportAlertDialog = AlertDialog
                .Builder(activity)
                .setView(exportView)
                .create()
        presenter.onAttach(this)

        exportView.exportDialogWipeData.setOnClickListener { wipeDataClick() }
        exportView.exportDialogOk.onClick { exportDataOk() }
        exportView.exportDialogCancel.onClick { exportDataClose() }
        view.settingsExportData.setOnClickListener { exportDataClick() }
        initExportGroup()
        return view
    }

    private fun exportDataClick() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return exportAlertDialog
            }
        })
    }

    private fun exportDataOk() {
        presenter.submitExportDatabase(destinationPath, getFilename(), isWipeData)
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

    override fun exportDatabaseSuccessfully() {
        context.toast(context.getString(R.string.export_database_success))
        exportAlertDialog.dismiss()
        if (isWipeData) router.pushController(RouterTransaction.with(TourController()))
    }


    private fun showFileChooserPreview() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            chooseFolderDialog()
        } else {
            requestExternalStoragePermission(mainActivity)
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFileChooserPreview()
            } else {
                // Permission request was denied.
            }
        }
    }

    private fun setUpToolbar() {
        settingsToolbar?.let {
            it.title = context.getString(R.string.settings_title)
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }


    private fun exportDataClose() = exportAlertDialog.dismiss()

    private fun wipeDataClick() {
        isWipeData = true
    }

    private fun getFilename(): String {
        val fileName = exportView.ExportDialogFileName.text.toString()
        return if (fileName.isBlank()) {
            "Umbrella"
        } else
            fileName
    }
}