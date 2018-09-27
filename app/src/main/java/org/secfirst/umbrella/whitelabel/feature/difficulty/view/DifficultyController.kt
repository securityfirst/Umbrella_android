package org.secfirst.umbrella.whitelabel.feature.difficulty.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.difficulty_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.difficulty.DaggerDifficultyComponent
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.presenter.DifficultyBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import javax.inject.Inject

class DifficultyController(bundle: Bundle) : BaseController(bundle), DifficultyView {

    @Inject
    internal lateinit var presenter: DifficultyBasePresenter<DifficultyView, DifficultyBaseInteractor>
    private val difficultClick: (Difficulty.Item) -> Unit = this::onDifficultClick
    private val categoryId by lazy { args.getLong(EXTRA_SELECTED_SEGMENT) }
    private val difficultyAdapter: DifficultyAdapter = DifficultyAdapter(difficultClick)

    constructor(idReference: Long) : this(Bundle().apply {
        putLong(EXTRA_SELECTED_SEGMENT, idReference)
    })

    companion object {
        const val EXTRA_SELECTED_SEGMENT = "selected_difficulty"
    }

    override fun onInject() {
        DaggerDifficultyComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    private fun onDifficultClick(difficulty: Difficulty.Item) {
        presenter.saveDifficultySelected(difficulty.idReference)
        presenter.submitLoadSegments(categoryId)
    }

    override fun startSegment(segments: List<Segment>) {
        router.pushController(RouterTransaction.with(SegmentController(segments)))
    }

    override fun onAttach(view: View) {
        presenter.onAttach(this)
        presenter.submitSelectDifficult(categoryId)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        difficultyAdapter.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.difficulty_view, container, false)
    }

    override fun showDifficulties(difficulty: Difficulty) {
        setUpToolbar(difficulty.titleToolbar)
        difficultyRecyclerView?.let {
            it.layoutManager = LinearLayoutManager(context)
            difficultyAdapter.addAll(difficulty.items)
            it.adapter = difficultyAdapter
        }
    }

    private fun setUpToolbar(toolbarTitle: String) {
        difficultyToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = toolbarTitle
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}