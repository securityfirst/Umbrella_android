package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.*
import android.widget.AdapterView
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.segment_item.*
import kotlinx.android.synthetic.main.segment_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.ChecklistController
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
    private val footClick: () -> Unit = this::onFootClicked
    private lateinit var difficultAdapter: DifficultSpinnerAdapter
    private val selectDifficulty by lazy { args.getParcelable(EXTRA_SEGMENT_BY_DIFFICULTY) as Difficulty? }
    private val selectModule by lazy { args.getParcelable(EXTRA_SEGMENT_BY_MODULE) as Module? }

    constructor(difficulty: Difficulty) : this(Bundle().apply {
        putParcelable(EXTRA_SEGMENT_BY_DIFFICULTY, difficulty)
    })

    constructor(module: Module) : this(Bundle().apply {
        putParcelable(EXTRA_SEGMENT_BY_MODULE, module)
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


        if (selectModule != null) {
            selectModule?.let { presenter.submitLoadSegments(it) }
        } else {
            selectDifficulty?.let { presenter.submitLoadSegments(it) }
        }

        setUpToolbar()
        onFavoriteClick()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.segment_view, container, false)
    }

    override fun showSegmentDetail(markdown: Markdown) {
        router.pushController(RouterTransaction.with(SegmentDetailController(markdown)))
    }

    override fun showSegments(segments: List<Segment>) {
        initSegmentView(segments)
    }

    private fun initSegmentView(segments: List<Segment>) {
        val segmentAdapter = SegmentAdapter(segmentClick, footClick)
        segmentRecyclerView?.initGridView(segmentAdapter)
        setFooterList(segmentAdapter)
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

    private fun setFooterList(segmentAdapter: SegmentAdapter) {
        val manager = segmentRecyclerView?.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (segmentAdapter.isHeader(position)) manager.spanCount else 1
            }
        }
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

    private fun onFootClicked() {
        router.pushController(RouterTransaction.with(ChecklistController()))
    }

    private fun onSegmentClicked(markdown: Markdown) {
        router.pushController(RouterTransaction.with(SegmentDetailController(markdown)))
    }


    companion object {
        const val EXTRA_SEGMENT_BY_MODULE = "selected_module"
        const val EXTRA_SEGMENT_BY_DIFFICULTY = "selected_difficulty"
    }
}