package org.secfirst.umbrella.whitelabel.feature.difficulty.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.difficult_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficult
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.difficulty.DaggerDifficultyComponent
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.presenter.DifficultyBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import javax.inject.Inject

class DifficultyController(bundle: Bundle) : BaseController(bundle), DifficultyView {

    @Inject
    internal lateinit var presenter: DifficultyBasePresenter<DifficultyView, DifficultyBaseInteractor>
    private val difficultClick: (Difficult) -> Unit = this::onDifficultClick
    private val categoryId by lazy { args.getLong(EXTRA_SELECTED_SEGMENT) }
    private val difficultAdapter: DifficultAdapter = DifficultAdapter(difficultClick)

    constructor(categoryId: Long) : this(Bundle().apply {
        putLong(EXTRA_SELECTED_SEGMENT, categoryId)
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

    private fun onDifficultClick(difficult: Difficult) {
        router.pushController(RouterTransaction.with(SegmentController(difficult.idReference)))
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(view: View) {
        presenter.onAttach(this)
        enableArrowBack(true)
        presenter.submitSelectDifficult(categoryId)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.difficult_view, container, false)
    }

    override fun showDifficulties(difficulties: List<Difficult>) {
        difficultRecyclerView?.let {
            it.layoutManager = LinearLayoutManager(context)
            difficultAdapter.addAll(difficulties)
            it.adapter = difficultAdapter
        }
    }

    override fun onDestroyView(view: View) {
        enableArrowBack(false)
        setToolbarTitle(context.getString(R.string.lesson_title))
    }

    override fun getToolbarTitle() = ""

    override fun getEnableBackAction() = true
}