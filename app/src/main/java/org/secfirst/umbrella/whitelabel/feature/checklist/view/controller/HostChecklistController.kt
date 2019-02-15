package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.raizlabs.android.dbflow.config.FlowManager
import kotlinx.android.synthetic.main.host_checklist.*
import kotlinx.android.synthetic.main.shake_device.view.*
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.HostChecklistAdapter
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
import javax.inject.Inject


class HostChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private lateinit var shakeDeviceView: View
    private lateinit var alertDialog: AlertDialog
    private val uriString by lazy { args.getString(EXTRA_ENABLE_DEEP_LINK_CHECKLIST) }

    constructor(uri: String = "") : this(Bundle().apply {
        putString(EXTRA_ENABLE_DEEP_LINK_CHECKLIST, uri)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        hostChecklistPager?.adapter = HostChecklistAdapter(this)
        hostChecklistTab?.setupWithViewPager(hostChecklistPager)
        shakeDeviceView.urlServerCancel.setOnClickListener { alertDialog.dismiss() }
        shakeDeviceView.urlServerOk.setOnClickListener { changeUrlServer() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.host_checklist, container, false)
        shakeDeviceView = inflater.inflate(R.layout.shake_device, container, false)
        alertDialog = AlertDialog
                .Builder(activity)
                .setView(shakeDeviceView)
                .create()
        presenter.onAttach(this)

        if (uriString.isNotBlank()) presenter.submitChecklistById(uriString)

//        view.toolbar.let {
//            mainActivity.setSupportActionBar(it)
//            mainActivity.supportActionBar?.title = context.getString(R.string.checklist_title)
//        }
        return view
    }

    override fun onDestroyView(view: View) {
        hostChecklistPager?.adapter = null
        hostChecklistTab?.setupWithViewPager(null)
        super.onDestroyView(view)
    }

    private fun changeUrlServer() {
        TentConfig.uriRepository = shakeDeviceView.urlServer.text.toString()
        FileUtils.deleteQuietly(context.cacheDir)
        FlowManager.getDatabase(AppDatabase.NAME).reset()
        router.pushController(RouterTransaction.with(TourController()))
        alertDialog.dismiss()
    }

    companion object {
        private const val EXTRA_ENABLE_DEEP_LINK_CHECKLIST = "deeplink"
    }

    override fun getChecklist(checklist: Checklist) {
        router.pushController(RouterTransaction.with(ChecklistController(checklist.id)))
    }
}
