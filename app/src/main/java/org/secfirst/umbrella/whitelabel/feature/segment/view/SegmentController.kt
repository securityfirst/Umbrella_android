package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.*
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.segment_item.*
import kotlinx.android.synthetic.main.segment_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.SegmentAdapter
import org.secfirst.umbrella.whitelabel.misc.initGridView
import javax.inject.Inject


class SegmentController(bundle: Bundle) : BaseController(bundle), SegmentView {


    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val segmentClick: (Int) -> Unit = this::onSegmentClicked
    private val footClick: () -> Unit = this::onFootClicked
    private val markdowns by lazy { args.getParcelableArray(EXTRA_SEGMENT) as Array<Markdown> }
    private val titleTab by lazy { args.getString(EXTRA_SEGMENT_TAB_TITLE) }
    private var indexTab = 0
    lateinit var hostSegmentTabControl: HostSegmentTabControl

    constructor(markdowns: List<Markdown>, titleTab: String) : this(Bundle().apply {
        putParcelableArray(EXTRA_SEGMENT, markdowns.toTypedArray())
        putString(EXTRA_SEGMENT_TAB_TITLE, titleTab)
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
        showSegmentView(markdowns.toList())
        onFavoriteClick()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.segment_view, container, false)
    }

    private fun showSegmentView(markdowns: List<Markdown>) {
        initSegmentView(markdowns)
    }

    private fun initSegmentView(markdowns: List<Markdown>) {
        val segmentAdapter = SegmentAdapter(segmentClick, footClick, markdowns.toMutableList())
        segmentRecyclerView?.initGridView(segmentAdapter)
        setFooterList(segmentAdapter)
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

    override fun showSegmentDetail(markdown: Markdown) {
        parentController?.router?.pushController(RouterTransaction.with(SegmentDetailController(markdown)))
    }

    private fun onFavoriteClick() {
        favoriteImg?.let {

        }
    }

    private fun onFootClicked() {
        // router.pushController(RouterTransaction.with(ChecklistController()))
    }

    private fun onSegmentClicked(position: Int) {
        hostSegmentTabControl.onTabHostManager(position + 1)
    }


    companion object {
        const val EXTRA_SEGMENT = "selected_segment"
        const val EXTRA_SEGMENT_TAB_TITLE = "selected_tab_title"
    }

    fun getTitle(): String = titleTab

    fun setIndexTab(position: Int) {
        this.indexTab = position
    }
}