package org.secfirst.umbrella.whitelabel.feature.lesson.view.controller

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.segment_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.lesson.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.lesson.DaggerLessonComponent
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.presenter.LessonBasePresenter
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.feature.lesson.view.adapter.SegmentAdapter
import org.secfirst.umbrella.whitelabel.feature.lesson.view.controller.DifficultController.Companion.EXTRA_SELECTED_SEGMENT
import javax.inject.Inject


class SegmentController(bundle: Bundle) : BaseController(bundle), LessonView {
    @Inject
    internal lateinit var presenter: LessonBasePresenter<LessonView, LessonBaseInteractor>
    private val segments by lazy { args.getParcelableArray(EXTRA_SELECTED_SEGMENT) }
    private val segmentClick: (Segment) -> Unit = this::onSegmentClicked
    private val adapter = SegmentAdapter(segmentClick)


    constructor(segments: List<Segment>) : this(Bundle().apply {
        putParcelableArray(EXTRA_SELECTED_SEGMENT, segments.toTypedArray())
    })

    private fun onSegmentClicked(segment: Segment) {

    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onAttach(this)
        setUpToolbar()
        showAllSegments()
    }

    @Suppress("UNCHECKED_CAST")
    private fun setUpToolbar() {
        val segmentList = segments as Array<Segment>
        segmentToolbar?.let { it.title = "${segmentList.last().title} $segmentList.last().titleToolbar" }
    }

    override fun onInject() {
        DaggerLessonComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun getEnableBackAction() = false

    override fun getToolbarTitle() = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.segment_view, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    fun showAllSegments() {
        val mGridLayoutManager = GridLayoutManager(context, 2)
        val segmentsList = segments as Array<Segment>
        adapter.addAll(segmentsList.toList())
        segmentRecyclerView?.let {
            it.layoutManager = mGridLayoutManager
            it.adapter = adapter
        }
    }
}