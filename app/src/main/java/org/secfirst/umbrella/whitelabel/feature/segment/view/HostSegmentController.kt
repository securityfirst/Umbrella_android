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
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.toControllers
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegmentController
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
    private val selectDifficulty by lazy { args.getParcelable(EXTRA_SEGMENT_BY_DIFFICULTY) as Difficulty? }
    private val selectModule by lazy { args.getParcelable(EXTRA_SEGMENT_BY_MODULE) as Module? }
    private val selectSubject by lazy { args.getParcelable(EXTRA_SEGMENT_BY_SUBJECT) as Subject? }

    private lateinit var hostAdapter: HostSegmentAdapter

    constructor(difficulty: Difficulty) : this(Bundle().apply {
        putParcelable(EXTRA_SEGMENT_BY_DIFFICULTY, difficulty)
    })

    constructor(module: Module) : this(Bundle().apply {
        putParcelable(EXTRA_SEGMENT_BY_MODULE, module)
    })

    constructor(subject: Subject) : this(Bundle().apply {
        putParcelable(EXTRA_SEGMENT_BY_SUBJECT, subject)
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

        when {
            selectModule != null -> selectModule?.let { presenter.submitLoadModule(it) }
            selectDifficulty != null -> selectDifficulty?.let { presenter.submitLoadSegments(it) }
            else -> selectSubject?.let { presenter.submitLoadSubject(it) }
        }
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.host_segment_view, container, false)
    }


    override fun showSegments(difficulties: MutableList<Difficulty>) {
        initSpinner(difficulties)
    }

    private fun initSpinner(difficulties: MutableList<Difficulty>) {
        val difficultAdapter = DifficultSpinnerAdapter(context, difficulties)
        hostSegmentSpinner?.let {
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    hostSegmentTab?.getTabAt(0)?.select()
                    refreshView(difficulties[position])
                    saveDifficultySelect()
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
        controllers.addAll(difficulty.markdowns.toControllers())
        controllers.addAll(difficulty.checklist.toChecklistControllers())
        hostSegmentPager?.let {
            it.adapter = hostAdapter
            it.offscreenPageLimit = PAGE_LIMIT
            hostSegmentTab?.setupWithViewPager(it)
        }
    }

    override fun onTabHostManager(position: Int) {
        hostSegmentTab?.getTabAt(position)?.select()
    }

    companion object {
        const val EXTRA_SEGMENT_BY_MODULE = "selected_module"
        const val EXTRA_SEGMENT_BY_DIFFICULTY = "selected_difficulty"
        const val EXTRA_SEGMENT_BY_SUBJECT = "selected_subject"
        private const val PAGE_LIMIT = 8
    }

    private fun setUpToolbar() {
        hostSegmentToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun saveDifficultySelect() {
        selectDifficulty?.let { difficulty ->
            difficulty.subject?.let { subject ->
                presenter.submitDifficultySelected(subject.id, difficulty)
            }
        }
    }
}