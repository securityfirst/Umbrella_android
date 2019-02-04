package org.secfirst.umbrella.whitelabel.feature.segment.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.host_segment_view.*
import kotlinx.android.synthetic.main.host_segment_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegmentController
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegmentDetailControllers
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.ChecklistController
import org.secfirst.umbrella.whitelabel.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.FilterAdapter
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.HostSegmentAdapter
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class HostSegmentController(bundle: Bundle) : BaseController(bundle), SegmentView, HostSegmentTabControl {

    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val markdownIds by lazy { args.getStringArrayList(EXTRA_MARKDOWN_IDS_HOST_SEGMENT) }
    private val enableFilter by lazy { args.getBoolean(EXTRA_ENABLE_FILTER_HOST_SEGMENT) }
    private lateinit var hostAdapter: HostSegmentAdapter


    constructor(markdownIds: ArrayList<String>, enableFilter: Boolean) : this(Bundle().apply {
        putSerializable(EXTRA_MARKDOWN_IDS_HOST_SEGMENT, markdownIds)
        putBoolean(EXTRA_ENABLE_FILTER_HOST_SEGMENT, enableFilter)
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
        initView(view)
        return view
    }

    private fun initView(view: View) {
        setUpToolbar(view)
        if (enableFilter) {
            view.hostSegmentSpinner.visibility = VISIBLE
            presenter.submitDifficulties(markdownIds)
        } else {
            presenter.submitMarkdowns(markdownIds)
            view.hostSegmentSpinner.visibility = INVISIBLE
        }
    }

    override fun showSegmentsWithDifficulty(difficulties: List<Difficulty>) = pickUpDifficulty(difficulties)

    override fun showSegments(markdowns: List<Markdown>) {
        if (markdowns.isNotEmpty())
            presenter.submitTitleToolbar(markdowns.last().subject?.id
                    ?: "", markdowns.last().module?.id ?: "")
        loadSegmentPages(markdowns, mutableListOf())
    }

    private fun pickUpDifficulty(difficulties: List<Difficulty>) {
        val difficultAdapter = FilterAdapter(context, difficulties)
        hostSegmentSpinner?.let {
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    hostSegmentTab?.getTabAt(0)?.select()
                    loadSegmentPages(difficulties[position].markdowns, difficulties[position].checklist)
                    saveDifficultySelect(difficulties[position])
                }
            }
            it.adapter = difficultAdapter
        }
    }

    private fun loadSegmentPages(markdowns: List<Markdown>, checklist: List<Checklist>) {
        launchSilent(uiContext) {
            val pageContainer = mutableListOf<BaseController>()
            val mainPage = markdowns.toSegmentController(checklist)
            val segmentPageLimit = markdowns.size
            pageContainer.add(mainPage)
            pageContainer.addAll(markdowns.toSegmentDetailControllers())
            pageContainer.addAll(checklist.toChecklistControllers())
            hostAdapter = HostSegmentAdapter(this@HostSegmentController, pageContainer, segmentPageLimit)

            hostSegmentPager?.let {
                it.adapter = hostAdapter
                hostSegmentTab?.setupWithViewPager(it)
            }
        }
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
        hostSegmentToolbar?.title = if (title.isEmpty()) context
                .getString(R.string.bookemarks_title) else title
    }

    override fun moveTabAt(position: Int) {
        hostSegmentTab?.getTabAt(position)?.select()
    }

    companion object {
        private const val EXTRA_MARKDOWN_IDS_HOST_SEGMENT = "ids"
        private const val EXTRA_ENABLE_FILTER_HOST_SEGMENT = "filter"
    }
}