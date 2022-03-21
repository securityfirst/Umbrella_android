package org.secfirst.umbrella.feature.segment.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.host_segment_view.*
import kotlinx.android.synthetic.main.host_segment_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.data.database.segment.toSegmentController
import org.secfirst.umbrella.data.database.segment.toSegmentDetailControllers
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.checklist.view.controller.ChecklistController
import org.secfirst.umbrella.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.feature.segment.view.SegmentView
import org.secfirst.umbrella.feature.segment.view.adapter.FilterAdapter
import org.secfirst.umbrella.feature.segment.view.adapter.HostSegmentAdapter
import javax.inject.Inject

class HostSegmentController(bundle: Bundle) : BaseController(bundle), SegmentView, HostSegmentTabControl {

    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val objectIds by lazy { args.getStringArrayList(EXTRA_OBJECT_IDS_HOST_SEGMENT) }
    private val enableFilter by lazy { args.getBoolean(EXTRA_ENABLE_FILTER_HOST_SEGMENT) }
    private val isFromDashboard by lazy { args.getBoolean(EXTRA_DASHBOARD) }
    private lateinit var hostAdapter: HostSegmentAdapter
    private lateinit var hostView: View
    private val uriString by lazy { args.getString(EXTRA_ENABLE_DEEP_LINK_SEGMENT) }


    constructor(objectIds: ArrayList<String>, enableFilter: Boolean, isFromDashboard: Boolean = false) : this(Bundle().apply {
        putSerializable(EXTRA_OBJECT_IDS_HOST_SEGMENT, objectIds)
        putBoolean(EXTRA_ENABLE_FILTER_HOST_SEGMENT, enableFilter)
        putBoolean(EXTRA_DASHBOARD, isFromDashboard)
    })

    constructor(uri: String) : this(Bundle().apply {
        putString(EXTRA_ENABLE_DEEP_LINK_SEGMENT, uri)
    })

    override fun onInject() {
        DaggerSegmentComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        hostView = inflater.inflate(R.layout.host_segment_view, container, false)
        presenter.onAttach(this)
        initView(hostView)
        hostView.hostSegmentTab.setupWithViewPager(hostView.hostSegmentPager)
        return hostView
    }

    private fun initView(view: View) {
        setUpToolbar(view)
        objectIds?.let {
            when {
                uriString != null -> presenter.submitMarkdownsByURI(uriString!!)

                enableFilter -> {
                    view.hostSegmentSpinner.visibility = VISIBLE
                    presenter.submitDifficulties(it)
                }

                else -> {
                    presenter.submitMarkdowns(it)
                    view.hostSegmentSpinner.visibility = INVISIBLE
                }
            }
        }
    }

    override fun showSegmentsWithDifficulty(difficulties: List<Difficulty>, markdownIndexSelected: Int) {
        loadDifficulties(difficulties, markdownIndexSelected)
    }

    override fun showSegments(markdowns: List<Markdown>, markdownIndexSelected: Int) {
        if (markdowns.isNotEmpty())
            presenter.submitTitleToolbar(markdowns.last().subject?.id
                    ?: "", markdowns.last().module?.id ?: "")
        loadSegmentPages(markdowns, mutableListOf())
        if (markdownIndexSelected > 0)
            moveTabAt(markdownIndexSelected)
    }

    private fun loadDifficulties(difficulties: List<Difficulty>, markdownIndexSelected: Int) {
        val difficultAdapter = FilterAdapter(context, difficulties)
        hostSegmentSpinner?.let {
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    hostSegmentTab?.getTabAt(0)?.select()
                    itemSelected(difficulties, markdownIndexSelected, position)
                }
            }
            it.adapter = difficultAdapter
        }
    }

    private fun itemSelected(difficulties: List<Difficulty>, markdownIndexSelected: Int, position: Int) {
        loadSegmentPages(difficulties[position].markdowns, difficulties[position].checklist)
        saveDifficultySelect(difficulties[position])
        if (markdownIndexSelected > 0)
            moveTabAt(markdownIndexSelected)
    }

    private fun loadSegmentPages(markdowns: List<Markdown>, checklist: List<Checklist>) {
        val pageContainer = mutableListOf<BaseController>()
        val mainPage = markdowns.toSegmentController(checklist, isFromDashboard)
        val segmentPageLimit = markdowns.size
        pageContainer.add(mainPage)
        pageContainer.addAll(markdowns.toSegmentDetailControllers())
        pageContainer.addAll(checklist.toChecklistControllers())
        hostAdapter = HostSegmentAdapter(this, pageContainer, segmentPageLimit)
        hostSegmentPager?.adapter = hostAdapter
    }

    private fun List<Checklist>.toChecklistControllers(): List<ChecklistController> {
        val controllers = mutableListOf<ChecklistController>()
        this.forEach { checklist ->
            val checklists = mutableListOf<Checklist>()
            checklists.add(checklist)
            val controller = ChecklistController(checklist.id, true)
            controllers.add(controller)
        }
        return controllers
    }

    private fun setUpToolbar(view: View) {
        view.hostSegmentToolbar.let {
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

    override fun getTitleToolbar(title: String) {
        hostView.hostSegmentToolbar.title = if (title.isEmpty()) context
                .getString(R.string.bookemarks_title) else title
    }

    override fun moveTabAt(position: Int) {
        hostView.hostSegmentTab.getTabAt(position)?.select()
    }

    companion object {
        private const val EXTRA_OBJECT_IDS_HOST_SEGMENT = "ids"
        private const val EXTRA_ENABLE_FILTER_HOST_SEGMENT = "filter"
        private const val EXTRA_ENABLE_DEEP_LINK_SEGMENT = "deeplink"
        const val EXTRA_DASHBOARD = "from_dashboard"
    }
}