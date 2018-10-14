package org.secfirst.umbrella.whitelabel.feature.checklist.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.github.tbouron.shakedetector.library.ShakeDetector
import com.raizlabs.android.dbflow.config.FlowManager
import kotlinx.android.synthetic.main.host_checklist.*
import kotlinx.android.synthetic.main.shake_device.view.*
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.HostChecklistAdapter
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController


class HostChecklistController : BaseController() {

    private lateinit var shakeDeviceView: View
    private lateinit var alertDialog: AlertDialog

    override fun onInject() {
    }

    override fun onAttach(view: View) {
        hostChecklistPager?.adapter = HostChecklistAdapter(this)
        hostChecklistTab?.setupWithViewPager(hostChecklistPager)
        ShakeDetector.create(context) { initUrlServerDialog() }
        shakeDeviceView.urlServerCancel.setOnClickListener { alertDialog.dismiss() }
        shakeDeviceView.urlServerOk.setOnClickListener { changeUrlServer() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        shakeDeviceView = inflater.inflate(R.layout.shake_device, container, false)
        alertDialog = AlertDialog
                .Builder(activity)
                .setView(shakeDeviceView)
                .create()

        return inflater.inflate(R.layout.host_checklist, container, false)
    }

    override fun onDestroyView(view: View) {
        hostChecklistPager?.adapter = null
        hostChecklistTab?.setupWithViewPager(null)
        super.onDestroyView(view)
    }

    private fun initUrlServerDialog() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog("urlServer", object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return alertDialog
            }
        })
    }

    private fun changeUrlServer() {
        TentConfig.uriRepository = shakeDeviceView.urlServer.text.toString()
        FileUtils.deleteQuietly(context.cacheDir)
        FlowManager.getDatabase(AppDatabase.NAME).reset()
        router.pushController(RouterTransaction.with(TourController()))
        alertDialog.dismiss()
    }
}