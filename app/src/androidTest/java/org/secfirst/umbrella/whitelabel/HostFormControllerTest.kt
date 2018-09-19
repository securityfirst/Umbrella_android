package org.secfirst.umbrella.whitelabel

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.HostFormController
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity


@RunWith(AndroidJUnit4::class)
class HostFormControllerTest {

    private lateinit var router: Router
    private val form: Form = Form()

    @Rule
    @JvmField
    var testRule: ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)


    @Before
    fun setUp() {
        testRule.runOnUiThread {
            router = testRule.activity.router
            router.setRoot(RouterTransaction.with(HostFormController()))
        }
    }

    @Test
    fun should_return_valid_title_for_all_forms() {
        onView(withId(R.id.titleAllForm)).check(matches(
                withText(R.string.message_title_all_forms)))
    }

    @Test
    fun should_return_valid_title_for_active_forms() {
        onView(withId(R.id.titleActiveForm)).check(matches(
                withText(R.string.message_title_active_forms)))
    }

    @Test
    fun menu_should_be_visible_in_form_controller() {
        onView(withId(R.id.navigation)).check(matches(isDisplayed()))

    }
}