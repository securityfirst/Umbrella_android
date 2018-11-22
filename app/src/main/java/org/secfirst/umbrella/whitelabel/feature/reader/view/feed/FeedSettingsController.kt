package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_location_dialog.view.*
import kotlinx.android.synthetic.main.feed_settings_view.*
import kotlinx.android.synthetic.main.feed_settings_view.view.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.textColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.component.FeedLocationDialog
import org.secfirst.umbrella.whitelabel.component.RefreshIntervalDialog
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import javax.inject.Inject


class FeedSettingsController : BaseController(), ReaderView, FeedLocationDialog.FeedLocationListener,
        RefreshIntervalDialog.RefreshIntervalListener {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private lateinit var refreshIntervalView: View
    private lateinit var feedSourceDialog: FeedSourceDialog
    private lateinit var feedSourceAlertDialog: AlertDialog
    private var feedsCheckbox = listOf<FeedSource>()
    private lateinit var feedLocationView: View
    private var feedLocation: FeedLocation? = null
    private lateinit var feedLocationDialog: FeedLocationDialog
    private lateinit var feedRefreshIntervalDialog: RefreshIntervalDialog

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        presenter.onAttach(this)
        val mainView = inflater.inflate(R.layout.feed_settings_view, container, false)
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)
        feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)
        presenter.prepareView()

        mainView.setUndefinedFeed.setOnClickListener { onClickUndefinedFeed() }
        mainView.setRefreshInterval.setOnClickListener { onClickRefreshInterval() }
        mainView.setLocation.setOnClickListener { onClickFeedLocation() }

        feedLocationDialog = FeedLocationDialog(feedLocationView, this, this)
        return mainView
    }

    private fun onClickUndefinedFeed() {
        val feedsChecked = feedsCheckbox.filter { it.lastChecked }
        var stringLocation = ""
        feedLocation?.let { stringLocation = it.location }
        when {
            stringLocation.isBlank() -> onClickFeedLocation()
            feedsChecked.isEmpty() -> onClickFeedSource()
            else -> {
                feedLocation?.let { presenter.submitFeedRequest(it, feedsCheckbox) }
            }
        }
    }

    private fun onClickRefreshInterval() {
       feedRefreshIntervalDialog.show()
    }

    private fun onClickFeedLocation() {
        feedLocationDialog.startLocationView()
    }

    private fun onClickFeedSource() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return feedSourceAlertDialog
            }
        })
    }

    private fun feedSourceOK() {
        feedsCheckbox = feedSourceDialog.getFeedSourcesUpdated()
        val feedChecked = feedsCheckbox.filter { it.lastChecked }
        presenter.submitInsertFeedSource(feedsCheckbox)
        populateFeedSource(feedsCheckbox)
        var stringLocation = ""
        feedLocation?.let { stringLocation = it.location }

        if (stringLocation.isNotBlank() && feedChecked.isNotEmpty())
            feedLocation?.let { dispatchFeedRequest(it, feedsCheckbox) }
        else
            onClickFeedLocation()

        feedSourceAlertDialog.dismiss()
    }

    private fun populateFeedSource(feedsChecked: List<FeedSource>) {
        var feedCheckInString = ""
        feedsChecked.forEach { if (it.lastChecked) feedCheckInString += "- ${it.name}\n" }
        this.feedsCheckbox = feedsChecked

        if (feedCheckInString.isEmpty()) {
            feedSource?.textColor = ContextCompat.getColor(context, R.color.feedSources_color)
            feedSource?.text = context.getString(R.string.feed_source_label)
        } else {
            feedSource?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
            feedSource?.text = feedCheckInString
        }
    }

    override fun prepareView(feedSources: List<FeedSource>, refreshIntervalPosition: Int,
                             feedLocation: FeedLocation) {

        this.feedLocation = feedLocation
        this.feedsCheckbox = feedSources
        prepareRefreshInterval(refreshIntervalPosition)
        prepareFeedSource(feedSources)
        prepareFeedLocation(feedLocation)
        dispatchFeedRequest(feedLocation, feedSources, true)
    }

    private fun prepareFeedSource(feedSources: List<FeedSource>) {
        populateFeedSource(feedSources)
        feedSourceDialog = FeedSourceDialog(feedSources)
        val feedSourceView = feedSourceDialog.createView(AnkoContext.create(context, this, false))
        activity?.let {
            feedSourceAlertDialog = AlertDialog
                    .Builder(it)
                    .setView(feedSourceView)
                    .create()
        }
        feedSourceView.alertControlOk.setOnClickListener { feedSourceOK() }
        feedSourceView.alertControlCancel.setOnClickListener { feedSourceCancel() }
        setFeedSource?.setOnClickListener { onClickFeedSource() }
    }

    private fun prepareRefreshInterval(refreshPosition: Int) {
        feedRefreshIntervalDialog = RefreshIntervalDialog(refreshIntervalView, refreshPosition, this)
        feedRefreshInterval?.text = feedRefreshIntervalDialog.getCurrentChoice()
    }

    private fun prepareFeedLocation(feedLocation: FeedLocation) {
        if (feedLocation.location.isNotBlank()) {
            this.feedLocation = feedLocation
            feedViewLocation?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
            feedLocationView.autocompleteLocation.setText(feedLocation.location)
            feedViewLocation?.text = feedLocation.location
            feedLocationView.autocompleteLocation.setSelection(feedLocation.location.length)
        }
    }

    override fun onLocationSuccess(feedLocation: FeedLocation) {
        feedViewLocation?.text = feedLocation.location
        feedViewLocation?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
        val feedsChecked = feedsCheckbox.filter { it.lastChecked }
        if (feedsChecked.isNotEmpty())
            dispatchFeedRequest(feedLocation, feedsCheckbox)
        else
            onClickFeedSource()

        presenter.submitInsertFeedLocation(feedLocation)
    }

    override fun onRefreshIntervalSuccess(selectedPosition: Int, selectedInterval: String) {
        feedRefreshInterval?.text = selectedInterval
        presenter.submitPutRefreshInterval(selectedPosition)
    }

    override fun startFeedController(feedItemResponse: Array<FeedItemResponse>, isFirstRequest: Boolean) {
        feedProgress?.visibility = View.INVISIBLE
        if (!isFirstRequest)
            openFeedController(feedItemResponse)
        else if (feedItemResponse.isNotEmpty())
            openFeedController(feedItemResponse)
    }

    private fun openFeedController(feedItemResponse: Array<FeedItemResponse>) {
        feedLocation?.let {
            router?.pushController(RouterTransaction
                    .with(FeedController(feedItemResponse, it.location)))
        }
    }

    private fun dispatchFeedRequest(feedLocation: FeedLocation,
                                    feedsChecked: List<FeedSource>,
                                    isFirstRequest: Boolean = false) {
        feedProgress?.visibility = View.VISIBLE
        presenter.submitFeedRequest(feedLocation, feedsChecked, isFirstRequest)
    }

    override fun feedError() {
        feedProgress?.visibility = View.INVISIBLE
    }

    private fun feedSourceCancel() = feedSourceAlertDialog.dismiss()
}