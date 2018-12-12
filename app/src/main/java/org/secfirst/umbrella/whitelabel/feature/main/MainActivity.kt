package org.secfirst.umbrella.whitelabel.feature.main

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.github.tbouron.shakedetector.library.ShakeDetector
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.main_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.PREF_NAME
import org.secfirst.umbrella.whitelabel.feature.account.view.AccountController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.HostChecklistController
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.HostFormController
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonController
import org.secfirst.umbrella.whitelabel.feature.login.view.LoginController
import org.secfirst.umbrella.whitelabel.feature.reader.view.HostReaderController
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController
import org.secfirst.umbrella.whitelabel.misc.hideKeyboard
import org.secfirst.umbrella.whitelabel.misc.removeShiftMode


class MainActivity : AppCompatActivity() {

    lateinit var router: Router

    private fun performDI() = AndroidInjection.inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_view)
        performDI()
        initRoute(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        ShakeDetector.start()
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
        navigation.removeShiftMode()
        navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        router = Conductor.attachRouter(this, baseContainer, savedInstanceState)
        if (!router.hasRootController() && TentConfig.isRepCreate() && (!isLoggedUser())) {
            router.setRoot(RouterTransaction.with(HostChecklistController()))
            navigation.menu.getItem(2).isChecked = true
        } else if (isLoggedUser()) {
            router.setRoot(RouterTransaction.with(LoginController()))
        } else {
            router.setRoot(RouterTransaction.with(TourController()))
        }
    }

    private val navigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.navigation_feeds -> {
                        router.pushController(RouterTransaction.with(HostReaderController()))
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_forms -> {
                        router.pushController(RouterTransaction.with(HostFormController()))
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_checklists -> {
                        router.pushController(RouterTransaction.with(HostChecklistController()))
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

    override fun onStop() {
        super.onStop()
        ShakeDetector.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        ShakeDetector.destroy()
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }

    fun hideNavigation() = navigation?.let { it.visibility = INVISIBLE }

    fun showNavigation() = navigation?.let { it.visibility = VISIBLE }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {

                true
            }
            else -> super.onKeyUp(keyCode, event)
        }
    }

    private fun isLoggedUser(): Boolean {
        val shared = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return shared.getBoolean(AppPreferenceHelper.EXTRA_LOGGED_IN, false)
    }
}