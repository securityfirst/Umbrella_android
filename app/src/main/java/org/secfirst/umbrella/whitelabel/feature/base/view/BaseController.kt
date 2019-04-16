package org.secfirst.umbrella.whitelabel.feature.base.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity


abstract class BaseController(bundle: Bundle = Bundle()) : Controller(bundle), LayoutContainer {

    private fun inject() = onInject()
    lateinit var mainActivity: MainActivity
    lateinit var context: Context

    protected abstract fun onInject()

    init {
        inject()
    }

    override val containerView: View?
        get() = view

    override fun onContextAvailable(context: Context) {
        this.context = context
        activity.let { mainActivity = activity as MainActivity }
        super.onContextAvailable(context)
    }

    override fun onDestroyView(view: View) {
        clearFindViewByIdCache()
        super.onDestroyView(view)
    }

    fun enableNavigation(isNavigation: Boolean) =
            if (isNavigation) mainActivity.showNavigation() else mainActivity.hideNavigation()
}