package org.secfirst.umbrella.whitelabel.feature.base.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity

abstract class BaseController(bundle: Bundle = Bundle()) : Controller(bundle), LayoutContainer {

    init {
        inject()
    }

    private fun inject() = onInject()
    lateinit var mainActivity: MainActivity
    lateinit var context: Context

    protected abstract fun onInject()

    override val containerView: View?
        get() = view

    override fun onContextAvailable(context: Context) {
        this.context = context
        activity.let {
            mainActivity = activity as MainActivity
            mainActivity.setToolbarTitle(getToolbarTitle())
            mainActivity.enableToolbar(getEnableBackAction())
        }
        super.onContextAvailable(context)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        clearFindViewByIdCache()
    }

    protected abstract fun getEnableBackAction(): Boolean

    protected abstract fun getToolbarTitle(): String

    fun setToolbarTitle(title: String) = mainActivity.setToolbarTitle(title)

    fun disableNavigation() = mainActivity.hideNavigation()

    fun enableNavigation() = mainActivity.showNavigation()

    fun enableToolbar() = mainActivity.showToolbar()

    fun disableToolbar() = mainActivity.hideToolbar()

    fun enableArrowBack(enable: Boolean = false) = mainActivity.enableUpArrow(enable)
}