package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.segment_item.*
import kotlinx.android.synthetic.main.segment_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
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
    private val segmentClick: (Markdown) -> Unit = this::onSegmentClicked
    private lateinit var difficultAdapter: DifficultSpinnerAdapter
    private val segments by lazy { args.getParcelableArray(EXTRA_SELECTED_SEGMENT) }

    constructor(segments: List<Segment>) : this(Bundle().apply {
        putParcelableArray(EXTRA_SELECTED_SEGMENT, segments.toTypedArray())
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
        setUpToolbar()
        onFavoriteClick()
        showSegments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.segment_view, container, false)
    }

    @Suppress("UNCHECKED_CAST")
    private fun showSegments() {
        val segmentList = segments as Array<Segment>
        initSegmentView(segmentList.toList())
    }

    private fun initSegmentView(segments: List<Segment>) {
        val segmentAdapter = SegmentAdapter(segmentClick)
        segmentRecyclerView?.initGridView(segmentAdapter)
        difficultAdapter = DifficultSpinnerAdapter(context, segments)
        segmentSpinner?.let {
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    segmentAdapter.add(segments[position].markdowns)
                }
            }
            it.adapter = difficultAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }
        return true
    }


    private fun setUpToolbar() {
        segmentToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun onFavoriteClick() {
        favoriteImg?.let {

        }
    }

    private fun onSegmentClicked(markdown: Markdown) {
        router.pushController(RouterTransaction.with(SegmentDetailController(markdown)))
    }
}