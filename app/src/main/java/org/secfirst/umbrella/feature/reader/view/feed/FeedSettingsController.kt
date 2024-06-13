package org.secfirst.umbrella.feature.reader.view.feed

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.account_password_alert.view.*
import kotlinx.android.synthetic.main.account_skip_alert.view.*
import kotlinx.android.synthetic.main.feed_location_dialog.view.*
import kotlinx.android.synthetic.main.feed_settings_view.*
import kotlinx.android.synthetic.main.feed_settings_view.view.*
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.DialogManager
import org.secfirst.umbrella.component.RefreshIntervalDialog
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.network.FeedItemResponse
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.feature.reader.view.ReaderView
import org.secfirst.umbrella.misc.checkPasswordStrength
import javax.inject.Inject


class FeedSettingsController : BaseController(), ReaderView, FeedLocationDialog.FeedLocationListener,
        RefreshIntervalDialog.RefreshIntervalListener,
        FeedSourceDialog.FeedSourceListener {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private lateinit var refreshIntervalView: View
    private var feedsCheckbox = listOf<FeedSource>()
    private lateinit var feedLocationView: View
    private var feedLocation: FeedLocation? = null
    private lateinit var feedLocationDialog: FeedLocationDialog
    private lateinit var feedRefreshIntervalDialog: RefreshIntervalDialog
    private lateinit var feedSourceDialog: FeedSourceDialog
    private lateinit var passwordAlertDialog: AlertDialog
    private lateinit var passwordView: View
    private lateinit var skipPasswordDialog: AlertDialog
    private lateinit var skipPasswordView: View
    private var changeAllSettings = false


    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        presenter.isSkipPassword()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        presenter.onAttach(this)
        val mainView = inflater.inflate(R.layout.feed_settings_view, container, false)
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)
        feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)
        passwordView = inflater.inflate(R.layout.account_password_alert, container, false)
        skipPasswordView = inflater.inflate(R.layout.account_skip_alert, container, false)

        presenter.prepareView()

        passwordAlertDialog = AlertDialog
                .Builder(activity)
                .setView(passwordView)
                .create()

        skipPasswordDialog = AlertDialog
                .Builder(activity)
                .setView(skipPasswordView)
                .create()

        passwordView.alertPasswordSkip.setOnClickListener { clickOnSkipAlert() }
        passwordView.alertPasswordOk.setOnClickListener { passwordAlertOk() }
        passwordView.alertPasswordCancel.setOnClickListener { passwordAlertCancel() }
        skipPasswordView.cancel.setOnClickListener { skipAlertCancel() }
        skipPasswordView.ok.setOnClickListener { skipAlertOk() }

        mainView.setUndefinedFeed.setOnClickListener { onClickUndefinedFeed() }
        mainView.setRefreshInterval.setOnClickListener { onClickRefreshInterval() }
        mainView.setLocation.setOnClickListener { onClickFeedLocation() }
        mainView.setFeedSource.setOnClickListener { onClickFeedSource() }

        feedLocationDialog = FeedLocationDialog(feedLocationView, this)
        return mainView
    }

    private fun skipAlertCancel() = skipPasswordDialog.dismiss()

    private fun skipAlertOk() {
        presenter.setSkipPassword(true)
        skipPasswordDialog.dismiss()
    }

    private fun passwordAlertCancel() = passwordAlertDialog.dismiss()

    private fun passwordAlertOk() {
        val token = passwordView.alertPwText.text.toString()
        if (token.checkPasswordStrength(context))
            presenter.submitChangeDatabaseAccess(token)

        passwordAlertDialog.dismiss()
    }

    private fun clickOnSkipAlert() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return skipPasswordDialog
            }
        })
        passwordAlertDialog.dismiss()
    }


    private fun showPasswordDialog() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return passwordAlertDialog
            }
        })
    }


    private fun onClickUndefinedFeed() {
        changeAllSettings = true
        onClickRefreshInterval()
    }

    private fun onClickRefreshInterval() {
        feedRefreshIntervalDialog.show()
    }

    private fun onClickFeedLocation() {
        feedLocationDialog.show()
    }

    private fun onClickFeedSource() {
        feedSourceDialog.show()
    }

    private fun populateFeedSource(feedsChecked: List<FeedSource>) {
        var feedCheckInString = ""
        feedsChecked.forEach { if (it.lastChecked) feedCheckInString += "- ${it.name}\n" }
        this.feedsCheckbox = feedsChecked
        if (feedCheckInString.isEmpty()) {
            feedSource?.text = context.getString(R.string.feed_source_label)
        } else {
            feedSource?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
            feedSource?.text = feedCheckInString
        }
    }

    override fun prepareView(feedSources: List<FeedSource>, refreshInterval: String,
                             feedLocation: FeedLocation) {

        this.feedLocation = feedLocation
        this.feedsCheckbox = feedSources
        prepareRefreshInterval(refreshInterval)
        prepareFeedSource(feedSources)
        prepareFeedLocation(feedLocation)
        dispatchFeedRequest(feedLocation, feedSources, true)
    }

    private fun prepareFeedSource(feedSources: List<FeedSource>) {
        feedSourceDialog = FeedSourceDialog(feedSources, context, this)
        populateFeedSource(feedSources)
    }

    private fun prepareRefreshInterval(refreshInterval: String) {
        feedRefreshIntervalDialog = RefreshIntervalDialog(refreshIntervalView, refreshInterval, this)
        feedRefreshInterval?.text = context.getString(R.string.feed_text_min, refreshInterval)
    }

    private fun prepareFeedLocation(feedLocation: FeedLocation) {
        if (feedLocation.location.isNotBlank()) {
            this.feedLocation = feedLocation
            feedViewLocation?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
            feedLocationView.location.setText(feedLocation.location)
            feedViewLocation?.text = feedLocation.location
            feedLocationView.location.setSelection(feedLocation.location.length)
        }
    }

    override fun onLocationCancel() {
        changeAllSettings = false
        super.onLocationCancel()
    }

    override fun onLocationSuccess(feedLoc: FeedLocation) {
        feedViewLocation?.text = feedLoc.location
        feedViewLocation?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
        val feedsChecked = feedsCheckbox.filter { it.lastChecked }
        presenter.submitInsertFeedLocation(feedLoc)
        feedLocation = feedLoc
        if (changeAllSettings) onClickFeedSource()
        else
            if (feedsChecked.isNotEmpty())
                dispatchFeedRequest(feedLoc, feedsCheckbox)
    }

    override fun onFeedSourceCancel() {
        changeAllSettings = false
        super.onFeedSourceCancel()
    }

    override fun onFeedSourceSuccess(feedSources: List<FeedSource>) {
        changeAllSettings = false
        feedsCheckbox = feedSources
        val feedChecked = feedsCheckbox.filter { it.lastChecked }
        presenter.submitInsertFeedSource(feedsCheckbox)
        populateFeedSource(feedsCheckbox)
        var stringLocation = ""
        feedLocation?.let { stringLocation = it.location }
        if (stringLocation.isNotBlank() && feedChecked.isNotEmpty())
            feedLocation?.let { dispatchFeedRequest(it, feedsCheckbox) }
    }

    override fun onRefreshIntervalCancel() {
        changeAllSettings = false
        super.onRefreshIntervalCancel()
    }

    override fun onRefreshIntervalSuccess(selectedInterval: String) {
        if (selectedInterval.isNotEmpty()) {
            feedRefreshInterval?.text = context.getString(R.string.feed_text_min, selectedInterval)
            presenter.submitPutRefreshInterval(selectedInterval.toInt())
        }
        if (changeAllSettings) onClickFeedLocation()
        else {
            val feedChecked = feedsCheckbox.filter { it.lastChecked }
            if (feedChecked.isNotEmpty()) {
                feedLocation?.let { dispatchFeedRequest(it, feedChecked) }
            }
        }
    }

    override fun startFeedController(feedItemResponse: Array<FeedItemResponse>, isFirstRequest: Boolean) {

        if (!isFirstRequest)
            openFeedController(feedItemResponse)
        else if (feedItemResponse.isNotEmpty())
            openFeedController(feedItemResponse)
    }

    private fun openFeedController(feedItemResponse: Array<FeedItemResponse>) {
        with(feedLocation) {
            if (this != null)
                router?.pushController(RouterTransaction
                        .with(FeedController(feedItemResponse)))
        }
    }

    private fun dispatchFeedRequest(feedLocation: FeedLocation,
                                    feedsChecked: List<FeedSource>,
                                    isFirstRequest: Boolean = false) {

        presenter.submitFeedRequest(feedLocation, feedsChecked, isFirstRequest)
    }

    override fun feedError() {

    }

    override fun isChangedToken(res: Boolean) {
        if (res) context.toast(context.getString(R.string.password_changed))
        else context.toast(context.getString(R.string.error_password))
    }

    override fun isSkipPassword(res: Boolean) {
        if (!res) showPasswordDialog()
    }
}