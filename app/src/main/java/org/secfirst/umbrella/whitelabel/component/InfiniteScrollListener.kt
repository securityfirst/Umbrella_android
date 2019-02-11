package org.secfirst.umbrella.whitelabel.component

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class InfiniteScrollListener(private var linearLayoutManager: LinearLayoutManager?) :
        RecyclerView.OnScrollListener() {
    // The total number of items in the dataset after the last load
    private var previousTotal = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // The minimum amount of items to have below your current scroll position before loading more.
    private val visibleThreshold = 8
    private var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var currentPage = 0
    private val loadMore = Runnable { onLoadMore(currentPage) }

    fun setLinearLayoutManager(linearLayoutManager: LinearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleItemCount = recyclerView.childCount
        totalItemCount = linearLayoutManager!!.itemCount
        firstVisibleItem = linearLayoutManager!!.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal || totalItemCount == 0) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        // End has been reached
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            currentPage++
            recyclerView.post(loadMore)
            loading = true
        }
    }

    abstract fun onLoadMore(currentPage: Int)
}
