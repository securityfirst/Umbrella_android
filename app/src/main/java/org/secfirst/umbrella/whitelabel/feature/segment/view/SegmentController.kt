package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.segment_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyController.Companion.EXTRA_SELECTED_SEGMENT
import org.secfirst.umbrella.whitelabel.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.misc.initGridView
import javax.inject.Inject


class SegmentController(bundle: Bundle) : BaseController(bundle), SegmentView {

    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val categoryId by lazy { args.getLong(EXTRA_SELECTED_SEGMENT) }
    private val segmentClick: (Segment.Item) -> Unit = this::onSegmentClicked
    private lateinit var difficultAdapter: DifficultSpinnerAdapter

    constructor(subcategoryId: Long) : this(Bundle().apply {
        putLong(EXTRA_SELECTED_SEGMENT, subcategoryId)
    })

    override fun onInject() {
        DaggerSegmentComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onAttach(this)
        presenter.submitLoadSegments(categoryId)
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.segment_view, container, false)
    }


    override fun showSegments(segments: List<Segment>) {
        val segmentAdapter = SegmentAdapter(segmentClick)
        segmentRecyclerView?.initGridView(segmentAdapter)
        difficultAdapter = DifficultSpinnerAdapter(context, android.R.layout.simple_dropdown_item_1line, segments)
        segmentSpinner?.let {
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    segmentAdapter.add(segments[position].items)
                }
            }
            it.adapter = difficultAdapter
        }
    }


    private fun setUpToolbar() {
        segmentToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun onSegmentClicked(segment: Segment.Item) {

    }
}