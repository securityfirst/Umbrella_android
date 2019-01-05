package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.host_segment_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.toChecklistControllers
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegmentController
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegmentDetailControllers
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.DifficultSpinnerAdapter
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.HostSegmentAdapter
import org.secfirst.umbrella.whitelabel.misc.TypeHelper
import javax.inject.Inject

class HostSegmentController(bundle: Bundle) : BaseController(bundle), SegmentView, HostSegmentTabControl {

    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val dataSelected by lazy { args.getSerializable(EXTRA_DATA_HOST_SEGMENT) }
    private lateinit var hostAdapter: HostSegmentAdapter

    constructor(dataSelected: Pair<TypeHelper, String>) : this(Bundle().apply {
        putSerializable(EXTRA_DATA_HOST_SEGMENT, dataSelected)
    })

    override fun onInject() {
        DaggerSegmentComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.host_segment_view, container, false)
        presenter.onAttach(this)
        submitData()
        setUpToolbar()
        return view
    }

    override fun showSegments(difficulties: MutableList<Difficulty>) {
        pickUpDifficulty(difficulties)
    }

    private fun submitData() {
        val pairSelected = dataSelected as Pair<TypeHelper, String>
        when (pairSelected.first.value) {
            TypeHelper.MODULE.value -> {
                presenter.submitLoadModule(pairSelected.second)
            }
            TypeHelper.SUBJECT.value -> {
                presenter.submitLoadSubject(pairSelected.second)
            }
            TypeHelper.DIFFICULTY.value -> {
                presenter.submitLoadSegments(pairSelected.second)
            }
        }
    }

    private fun pickUpDifficulty(difficulties: MutableList<Difficulty>) {
        val difficultAdapter = DifficultSpinnerAdapter(context, difficulties)
        hostSegmentSpinner?.let {
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    hostSegmentTab?.getTabAt(0)?.select()
                    refreshView(difficulties[position])
                    saveDifficultySelect(difficulties[position])
                }
            }
            it.adapter = difficultAdapter
        }
    }

    private fun refreshView(difficulty: Difficulty) {
        val controllers = mutableListOf<BaseController>()
        val selectSegment = difficulty.toSegmentController(this)
        val segmentPageLimit = difficulty.markdowns.size
        hostAdapter = HostSegmentAdapter(this, controllers, segmentPageLimit)
        controllers.add(selectSegment)
        controllers.addAll(difficulty.markdowns.toSegmentDetailControllers())
        controllers.addAll(difficulty.checklist.toChecklistControllers())
        hostSegmentPager?.let {
            it.adapter = hostAdapter
            it.offscreenPageLimit = segmentPageLimit
            hostSegmentTab?.setupWithViewPager(it)
        }
    }

    override fun onTabHostManager(position: Int) {
        hostSegmentTab?.getTabAt(position)?.select()
    }

    private fun setUpToolbar() {
        hostSegmentToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun saveDifficultySelect(spinnerSelected: Difficulty) {
        if (spinnerSelected.id.isNotEmpty())
            spinnerSelected.subject?.let { subject ->
                presenter.submitDifficultySelected(subject.id, spinnerSelected)
            }
    }

    companion object {
        private const val EXTRA_DATA_HOST_SEGMENT = "data_segment"
    }
}