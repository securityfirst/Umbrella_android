package org.secfirst.umbrella.whitelabel.feature.tour.view

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext
import org.secfirst.umbrella.whitelabel.UmbrellaApplication

class TourAdapter(private val controller: TourController) : PagerAdapter() {

    private val childrenViews = mutableListOf<TourUI>()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): View {
        val view = childrenViews[position].createView(AnkoContext.create(UmbrellaApplication.instance, controller, false))
        collection.addView(view)
        return view
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun setData(list: List<TourUI>?) {
        childrenViews.clear()
        if (list != null && !list.isEmpty()) {
            childrenViews.addAll(list)
        }

        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return childrenViews.size
    }
}