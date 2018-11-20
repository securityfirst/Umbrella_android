package org.secfirst.umbrella.whitelabel.feature.account.view

import Extensions.Companion.PERMISSION_REQUEST_EXTERNAL_STORAGE
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.codekidlabs.storagechooser.StorageChooser
import kotlinx.android.synthetic.main.account_settings_view.*
import kotlinx.android.synthetic.main.account_settings_view.view.*
import kotlinx.android.synthetic.main.settings_export_dialog.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.feature.account.DaggerAccountComponent
import org.secfirst.umbrella.whitelabel.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.account.presenter.AccountBasePresenter
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import requestExternalStoragePermission
import javax.inject.Inject

class SettingsController : BaseController(), AccountView {

    private lateinit var exportAlertDialog: AlertDialog
    private lateinit var exportView: View
    private lateinit var destinationPath: String
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

        exportView.exportDialogOk.onClick { exportDataOk() }
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
        presenter.submitExportDatabase(destinationPath,
                exportView.ExportDialogFileName.text.toString(),
                context.getDatabasePath(AppDatabase.NAME + ".db"))
    }

    private fun showFileChooserPreview() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            chooseFolderDialog()
        } else {
            requestExternalStoragePermission(mainActivity)
        }
    }

    private fun initExportGroup() {
        exportView.exportDialogGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.exportDialogTypeExport -> showFileChooserPreview()
                R.id.ExportDialogShareType -> ""
            }
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
            Log.e("SELECTED_PATH", path)
        }
    }

    private fun setUpToolbar() {
        settingsToolbar?.let {
            it.title = context.getString(R.string.title_settings)
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun exportDatabaseSuccessfully() {
        Toast.makeText(context, "Database exported with Successfully.", Toast.LENGTH_SHORT).show()
    }

}