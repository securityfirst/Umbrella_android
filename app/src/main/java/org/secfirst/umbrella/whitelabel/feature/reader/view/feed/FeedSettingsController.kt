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
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*
import kotlinx.android.synthetic.main.feed_location_dialog.view.*
import kotlinx.android.synthetic.main.feed_settings_view.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.textColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import org.secfirst.umbrella.whitelabel.misc.init
import javax.inject.Inject


class FeedSettingsController : BaseController(), ReaderView, FeedLocationListener {
    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private lateinit var refreshIntervalView: View
    private lateinit var feedSourceDialog: FeedSourceDialog
    private lateinit var refreshIntervalAlertDialog: AlertDialog
    private lateinit var feedSourceAlertDialog: AlertDialog
    private lateinit var feedLocationAutoText: FeedLocationAutoText
    private var feedsCheckbox = listOf<FeedSource>()
    private lateinit var feedLocationView: View
    private lateinit var feedLocationAlertDialog: AlertDialog
    private var feedLocation: FeedLocation? = null

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUndefinedFeed?.setOnClickListener { onClickUndefinedFeed() }
        setRefreshInterval?.setOnClickListener { onClickRefreshInterval() }
        setLocation?.setOnClickListener { onClickFeedLocation() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        presenter.onAttach(this)
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)
        feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)

        presenter.prepareView()

        feedLocationView.alertControlCancel.setOnClickListener { feedLocationCancel() }
        feedLocationView.alertControlOk.setOnClickListener { feedLocationOk() }
        refreshIntervalView.alertControlOk.setOnClickListener { refreshIntervalOk() }
        refreshIntervalView.alertControlCancel.setOnClickListener { refreshIntervalCancel() }

        refreshIntervalAlertDialog = AlertDialog
                .Builder(activity)
                .setView(refreshIntervalView)
                .create()
        feedLocationAlertDialog = AlertDialog
                .Builder(activity)
                .setView(feedLocationView)
                .create()

        return inflater.inflate(R.layout.feed_settings_view, container, false)
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
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return refreshIntervalAlertDialog
            }
        })
    }

    private fun onClickFeedLocation() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return feedLocationAlertDialog
            }
        })
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

    private fun refreshIntervalOk() {
        val intervalSelected = refreshIntervalView.refreshInterval.selectedItem.toString()
        feedRefreshInterval?.text = intervalSelected
        refreshIntervalAlertDialog.dismiss()
        val position = refreshIntervalView.refreshInterval.selectedItemPosition
        presenter.submitPutRefreshInterval(position)
    }

    private fun feedLocationOk() {
        val locationSelected = feedLocationView.autocompleteLocation.text.toString()
        feedViewLocation?.text = locationSelected
        feedViewLocation?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
        val newLocation = FeedLocation(1, locationSelected, feedLocationAutoText.getCountryCode())
        feedLocation = newLocation
        val feedsChecked = feedsCheckbox.filter { it.lastChecked }
        if (feedsChecked.isNotEmpty())
            dispatchFeedRequest(newLocation, feedsCheckbox)
        else
            onClickFeedSource()

        presenter.submitInsertFeedLocation(newLocation)
        feedLocationAlertDialog.dismiss()
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
        val spinner = refreshIntervalView.refreshInterval
        spinner.init(R.array.refresh_interval_array)
        refreshIntervalView.refreshInterval.setSelection(refreshPosition)
        feedRefreshInterval?.text = spinner.selectedItem.toString()
    }

    private fun prepareFeedLocation(feedLocation: FeedLocation) {
        populateFeedAutoText(feedLocation)
        if (feedLocation.location.isNotBlank()) {
            this.feedLocation = feedLocation
            feedViewLocation?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
            feedLocationView.autocompleteLocation.setText(feedLocation.location)
            feedViewLocation?.text = feedLocation.location
            feedLocationView.autocompleteLocation.setSelection(feedLocation.location.length)
        }
    }

    private fun populateFeedAutoText(feedLocation: FeedLocation) {
        val selectedPlace = mutableListOf<String>()
        selectedPlace.add(feedLocation.location)
        val locationInfo = LocationInfo(selectedPlace, feedLocation.iso2)
        feedLocationAutoText = FeedLocationAutoText(feedLocationView.autocompleteLocation, context, this)
        feedLocationAutoText.setLocationInfo(locationInfo)
    }

    override fun onTextChanged(text: String) {
        presenter.submitAutocompleteAddress(text)
    }

    override fun newAddressAvailable(locationInfo: LocationInfo) {
        if (locationInfo.locationNames.isNotEmpty())
            feedLocationAutoText.updateAddress(locationInfo)
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

    private fun refreshIntervalCancel() = refreshIntervalAlertDialog.dismiss()

    private fun feedSourceCancel() = feedSourceAlertDialog.dismiss()

    private fun feedLocationCancel() = feedLocationAlertDialog.dismiss()
}