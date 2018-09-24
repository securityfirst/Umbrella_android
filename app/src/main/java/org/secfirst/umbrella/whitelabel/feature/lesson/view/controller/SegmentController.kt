package org.secfirst.umbrella.whitelabel.feature.lesson.view.controller

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.segment_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.lesson.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.lesson.DaggerLessonComponent
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.presenter.LessonBasePresenter
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.feature.lesson.view.adapter.DifficultLevelAdapter
import org.secfirst.umbrella.whitelabel.feature.lesson.view.adapter.SegmentAdapter
import org.secfirst.umbrella.whitelabel.feature.lesson.view.controller.DifficultController.Companion.EXTRA_SELECTED_SEGMENT
import javax.inject.Inject


class SegmentController(bundle: Bundle) : BaseController(bundle), LessonView {

    @Inject
    internal lateinit var presenter: LessonBasePresenter<LessonView, LessonBaseInteractor>
    private val currentSegment by lazy { args.getParcelable(EXTRA_SELECTED_SEGMENT) as Segment }
    private val segmentClick: (Segment.Item) -> Unit = this::onSegmentClicked
    private lateinit var segments: ArrayList<Segment>
    private val segmentAdapter = SegmentAdapter(segmentClick, currentSegment.items.toMutableList())

    constructor(segment: Segment) : this(Bundle().apply {
        putParcelable(EXTRA_SELECTED_SEGMENT, segment)
    })

    override fun onInject() {
        DaggerLessonComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onAttach(this)
        showAllSegments()
        presenter.submitLoadLessonInSegment(currentSegment.idReference)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.segment_view, container, false)
    }


    override fun getEnableBackAction() = false

    override fun getToolbarTitle() = ""

    private fun onSegmentClicked(segment: Segment.Item) {

    }

    override fun showDifficultLevel(segmentList: ArrayList<Segment>) {
        segments = segmentList
        segmentSpinner?.let {
            it.adapter = DifficultLevelAdapter(context, android.R.layout.simple_dropdown_item_1line, segments)
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    segmentAdapter.add(segments[position].items)
                }
            }
        }
    }

    private fun showAllSegments() {
        val mGridLayoutManager = GridLayoutManager(context, 2)
        segmentRecyclerView?.let {
            it.layoutManager = mGridLayoutManager
            it.adapter = segmentAdapter
            it.setHasFixedSize(true)
        }
    }

}