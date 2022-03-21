package org.secfirst.umbrella.feature.search.view

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.searching_view.*
import org.secfirst.advancedsearch.interfaces.AdvancedSearchPresenter
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.search.DaggerSearchComponent
import org.secfirst.umbrella.feature.search.interactor.SearchBaseInteractor
import org.secfirst.umbrella.feature.search.presenter.SearchBasePresenter
import javax.inject.Inject

class SearchController(bundle: Bundle) : BaseController(bundle), SearchView {

    @Inject
    internal lateinit var presenter: SearchBasePresenter<SearchView, SearchBaseInteractor>
    private val query by lazy { args.getString(EXTRA_SEARCH_QUERY) }
    private lateinit var mainIntentAction: String

    constructor(query: String = "") : this(Bundle().apply {
        putString(EXTRA_SEARCH_QUERY, query)
    })

    override fun onInject() {
        DaggerSearchComponent.builder()
            .application(UmbrellaApplication.instance)
            .build()
            .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        presenter.onAttach(this)
        mainActivity.registerSearchProvider(presenter as AdvancedSearchPresenter)
        mainActivity.intent.action?.let { mainIntentAction = it }
        query?.let { presenter.submitSearchQuery(it) }
        mainActivity.hideNavigation()
        mainActivity.intent.action = Intent.ACTION_SEARCH
        mainActivity.intent.putExtra(SearchManager.QUERY, query)
        return inflater.inflate(R.layout.searching_view, container, false)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        mainActivity.intent.action = mainIntentAction
        mainActivity.showNavigation()
        mainActivity.releaseSearchProvider()
    }

    override fun handleBack(): Boolean {
        return super.handleBack()
    }

    private fun setUpToolbar() {
        searchToolbar?.let {
            it.setTitle(R.string.search_results)
            it.setNavigationIcon(R.drawable.ic_action_back)
            it.setNavigationOnClickListener { mainActivity.onBackPressed() }
        }
    }

    companion object {
        private const val EXTRA_SEARCH_QUERY = "search_query"
    }

}