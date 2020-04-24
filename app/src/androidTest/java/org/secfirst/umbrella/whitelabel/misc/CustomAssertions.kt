package org.secfirst.umbrella.misc

import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.matcher.ViewMatchers.assertThat
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.CoreMatchers

class CustomAssertions {
    companion object {
        fun hasItemCount(count: Int): ViewAssertion {
            return RecyclerViewItemCountAssertion(count)
        }
    }

    private class RecyclerViewItemCountAssertion(private val count: Int) : ViewAssertion {
        override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            if (view !is RecyclerView) {
                throw IllegalStateException("The asserted view is not RecyclerView")
            }

            if (view.adapter == null) {
                throw IllegalStateException("No adapter is assigned to RecyclerView")
            }

            assertThat("RecyclerView item count", view.adapter.itemCount, CoreMatchers.equalTo(count))
        }
    }
}