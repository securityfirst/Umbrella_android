package org.secfirst.umbrella.feature.checklist.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.host_checklist.*
import kotlinx.android.synthetic.main.host_checklist.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.feature.checklist.view.adapter.HostChecklistAdapter
import javax.inject.Inject


class HostChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>

    private val uriString by lazy { args.getString(EXTRA_ENABLE_DEEP_LINK_CHECKLIST) ?: "" }

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = inflater.inflate(R.layout.host_checklist, container, false)
        presenter.onAttach(this)
        if (uriString.isNotBlank()) presenter.submitChecklistById(uriString)
        view.toolbar.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.checklist_title)
        }
        mainActivity.navigationPositionToCenter()
        return view
    }

    override fun onDestroyView(view: View) {
        hostChecklistPager?.adapter = null
        hostChecklistTab?.setupWithViewPager(null)
        super.onDestroyView(view)
    }

    companion object {
        private const val EXTRA_ENABLE_DEEP_LINK_CHECKLIST = "deeplink"
    }

    override fun getChecklist(checklist: Checklist) {
        router.pushController(RouterTransaction.with(ChecklistController(checklist.id)))
    }
}
