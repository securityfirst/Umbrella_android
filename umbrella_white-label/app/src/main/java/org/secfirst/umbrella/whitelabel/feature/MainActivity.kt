package org.secfirst.umbrella.whitelabel.feature

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.main_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.storage.TentConfig
import org.secfirst.umbrella.whitelabel.feature.account.AccountController
import org.secfirst.umbrella.whitelabel.feature.feed.FeedController
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.HostFormController
import org.secfirst.umbrella.whitelabel.feature.lesson.LessonController
import org.secfirst.umbrella.whitelabel.feature.main.OnNavigationBottomView
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
import org.secfirst.umbrella.whitelabel.misc.hideKeyboard
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject


class MainActivity : AppCompatActivity(), OnNavigationBottomView {

    private lateinit var router: Router

    @Inject
    internal lateinit var tentConfig: TentConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view)
        performDI()
        initRoute(savedInstanceState)
        setUpToolbar()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun setUpToolbar() {
        setSupportActionBar(mainToolbar)
    }

    fun setToolBarTitle(title: String) {
        mainToolbar?.let { it.title = title }
    }

    fun enableUpArrow(enabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
        supportActionBar?.setDisplayShowHomeEnabled(enabled)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            hideKeyboard()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRoute(savedInstanceState: Bundle?) {
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        router = Conductor.attachRouter(this, baseContainer, savedInstanceState)
        if (!router.hasRootController() && tentConfig.isCreate())
            router.setRoot(RouterTransaction.with(FeedController()))
        else router.setRoot(RouterTransaction.with(TourController()))
    }

    private val navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.navigation_feeds -> {
                router.pushController(RouterTransaction.with(FeedController()))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_forms -> {
                router.pushController(RouterTransaction.with(HostFormController()))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_checklists -> {
                router.pushController(RouterTransaction.with(LessonController()))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_lessons -> {
                router.pushController(RouterTransaction.with(LessonController()))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_account -> {
                router.pushController(RouterTransaction.with(AccountController()))
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    private fun performDI() = AndroidInjection.inject(this)

    fun getRouter() = router

    override fun showBottomMenu() {
        navigation?.let { it.visibility = VISIBLE }
    }

    override fun hideBottomMenu() {
        navigation?.let { it.visibility = INVISIBLE }
    }

    override fun showToolbar() {
        supportActionBar?.show()
    }

    override fun hideToolbar() {
        supportActionBar?.hide()
    }

}
