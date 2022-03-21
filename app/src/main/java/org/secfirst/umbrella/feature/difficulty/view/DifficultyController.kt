package org.secfirst.umbrella.feature.difficulty.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.difficulty_view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.ItemDecoration
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.difficulty.DaggerDifficultyComponent
import org.secfirst.umbrella.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.feature.difficulty.presenter.DifficultyBasePresenter
import org.secfirst.umbrella.feature.segment.view.controller.HostSegmentController
import javax.inject.Inject

class DifficultyController(bundle: Bundle) : BaseController(bundle), DifficultyView {

    @Inject
    internal lateinit var presenter: DifficultyBasePresenter<DifficultyView, DifficultyBaseInteractor>
    private val difficultClick: (Int) -> Unit = this::onDifficultClick
    private val subjectId by lazy { args.getString(EXTRA_SELECTED_DIFFICULTY) }
    private val isDeepLink by lazy { args.getBoolean(EXTRA_IS_DEEP_LINK) }
    private val difficultyAdapter = DifficultyAdapter(difficultClick)

    constructor(subjectId: String, isDeepLink: Boolean = false) : this(Bundle().apply {
        putString(EXTRA_SELECTED_DIFFICULTY, subjectId)
        putBoolean(EXTRA_IS_DEEP_LINK, isDeepLink)
    })

    override fun onInject() {
        DaggerDifficultyComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        presenter.onAttach(this)
        subjectId?.let { presenter.submitDifficulty(it) }
        return inflater.inflate(R.layout.difficulty_view, container, false)
    }

    private fun onDifficultClick(position: Int) {
        val itemSelected = difficultyAdapter.getItem(position)
        subjectId?.let { presenter.saveDifficultySelect(itemSelected, it) }
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

    override fun showDifficulties(difficulties: List<Difficulty>, toolbarTitle: String) {
        setUpToolbar(toolbarTitle)
        difficultyAdapter.addAll(difficulties.toMutableList())
        difficultyRecyclerView?.let {
            it.addItemDecoration(ItemDecoration(resources!!.getDimensionPixelSize(R.dimen.decorator_card_padding), ItemDecoration.VERTICAL))
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

    companion object {
        const val EXTRA_SELECTED_DIFFICULTY = "selected_difficulty"
        const val EXTRA_IS_DEEP_LINK = "deeplink"
    }

}