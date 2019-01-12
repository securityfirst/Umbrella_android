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
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.difficulty.DaggerDifficultyComponent
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.presenter.DifficultyBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.HostSegmentController
import javax.inject.Inject

class DifficultyController(bundle: Bundle) : BaseController(bundle), DifficultyView {

    @Inject
    internal lateinit var presenter: DifficultyBasePresenter<DifficultyView, DifficultyBaseInteractor>
    private val difficultClick: (Int) -> Unit = this::onDifficultClick
    private val selectSubject by lazy { args.getParcelable(EXTRA_SELECTED_SEGMENT) as Subject }
    private lateinit var difficultyAdapter: DifficultyAdapter

    constructor(subject: Subject) : this(Bundle().apply {
        putParcelable(EXTRA_SELECTED_SEGMENT, subject)
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

    private fun onDifficultClick(position: Int) {
        val itemSelected = difficultyAdapter.getItem(position)
        presenter.saveDifficultySelect(itemSelected, selectSubject.id)
        presenter.submitDifficultySelect(difficultyAdapter.getItems(position))
    }

    override fun startSegment(difficultyIds: List<String>) {
        router.pushController(RouterTransaction.with(
                HostSegmentController(ArrayList(difficultyIds), true)))
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        difficultyAdapter.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        presenter.onAttach(this)
        presenter.submitDifficulty(selectSubject)
        return inflater.inflate(R.layout.difficulty_view, container, false)
    }

    override fun showDifficulties(difficulties: List<Difficulty>, toolbarTitle: String) {
        setUpToolbar(toolbarTitle)
        difficultyAdapter = DifficultyAdapter(difficulties.toMutableList(), difficultClick)
        difficultyRecyclerView?.let {
            it.layoutManager = LinearLayoutManager(context)
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