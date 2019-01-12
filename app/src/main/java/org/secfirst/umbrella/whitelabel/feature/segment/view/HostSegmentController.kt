package org.secfirst.umbrella.whitelabel.feature.segment.view

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
import org.secfirst.umbrella.whitelabel.data.database.checklist.toChecklistControllers
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegmentController
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegmentDetailControllers
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.DifficultSpinnerAdapter
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.HostSegmentAdapter
import javax.inject.Inject

class HostSegmentController(bundle: Bundle) : BaseController(bundle), SegmentView, HostSegmentTabControl {

    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val markdownIds by lazy { args.getStringArrayList(EXTRA_MARKDOWN_IDS_HOST_SEGMENT) }
    private val difficultiesIds by lazy { args.getStringArrayList(EXTRA_DIFFICULTY_IDS_HOST_SEGMENT) }
    private val enableFilter by lazy { args.getBoolean(EXTRA_ENABLE_FILTER_HOST_SEGMENT) }

    private lateinit var hostAdapter: HostSegmentAdapter

    constructor(difficultyIds: ArrayList<String>, enableFilter: Boolean) : this(Bundle().apply {
        putSerializable(EXTRA_DIFFICULTY_IDS_HOST_SEGMENT, difficultyIds)
        putBoolean(EXTRA_ENABLE_FILTER_HOST_SEGMENT, enableFilter)
    })

    constructor(markdownIds: ArrayList<String>) : this(Bundle().apply {
        putSerializable(EXTRA_MARKDOWN_IDS_HOST_SEGMENT, markdownIds)
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
        markdownIds?.let {
            presenter.submitMarkdowns(markdownIds)
            view.hostSegmentToolbar.title = context.getText(R.string.app_name)
        }
        difficultiesIds?.let { presenter.submitDifficulties(difficultiesIds) }

        if (enableFilter) view.hostSegmentSpinner.visibility = VISIBLE
        else view.hostSegmentSpinner.visibility = INVISIBLE
    }

    override fun showSegmentsWithDifficulty(difficulties: List<Difficulty>) = pickUpDifficulty(difficulties)

    override fun showSegments(markdown: List<Markdown>) = loadSegmentPages(markdown, mutableListOf())

    private fun pickUpDifficulty(difficulties: List<Difficulty>) {
        val difficultAdapter = DifficultSpinnerAdapter(context, difficulties)
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
        val pageContainer = mutableListOf<BaseController>()
        val mainPage = markdowns.toSegmentController(this, checklist)
        val segmentPageLimit = markdowns.size
        pageContainer.add(mainPage)
        pageContainer.addAll(markdowns.toSegmentDetailControllers())
        pageContainer.addAll(checklist.toChecklistControllers())
        hostAdapter = HostSegmentAdapter(this, pageContainer, segmentPageLimit)
        hostSegmentPager?.let {
            it.adapter = hostAdapter
            it.offscreenPageLimit = segmentPageLimit
            hostSegmentTab?.setupWithViewPager(it)
        }
    }

    override fun onTabHostManager(position: Int) {
        hostSegmentTab?.getTabAt(position)?.select()
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

    companion object {
        private const val EXTRA_MARKDOWN_IDS_HOST_SEGMENT = "markdown_ids"
        private const val EXTRA_DIFFICULTY_IDS_HOST_SEGMENT = "difficulty_ids"
        private const val EXTRA_ENABLE_FILTER_HOST_SEGMENT = "filter"
    }
}