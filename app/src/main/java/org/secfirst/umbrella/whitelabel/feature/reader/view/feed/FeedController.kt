package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*
import kotlinx.android.synthetic.main.feed_location_dialog.view.*
import kotlinx.android.synthetic.main.feed_view.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.textColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import org.secfirst.umbrella.whitelabel.misc.init
import javax.inject.Inject


class FeedController : BaseController(), ReaderView {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private lateinit var refreshIntervalView: View
    private lateinit var feedLocationView: View
    private lateinit var feedSourceDialog: FeedSourceDialog
    private lateinit var refreshIntervalAlertDialog: AlertDialog
    private lateinit var feedLocationAlertDialog: AlertDialog
    private lateinit var feedSourceAlertDialog: AlertDialog
    private lateinit var feedLocationAutoText: FeedLocationAutoText

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

        presenter.submitLoadFeedSources()
        presenter.submitLoadRefreshInterval()
        presenter.submitLoadFeedLocation()

        feedLocationAutoText = FeedLocationAutoText(feedLocationView.autocompleteLocation, context, presenter)
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

        return inflater.inflate(R.layout.feed_view, container, false)
    }

    private fun onClickUndefinedFeed() {
        onClickFeedSource()
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
        val feedsChecked = feedSourceDialog.getFeedSourcesUpdated()
        presenter.submitInsertFeedSource(feedsChecked)
        populateFeedSource(feedsChecked)
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
        presenter.submitInsertFeedLocation(FeedLocation(1, locationSelected,
                feedLocationAutoText.getCountryCode() ?: ""))
        feedLocationAlertDialog.dismiss()
    }

    private fun populateFeedSource(feedsChecked: List<FeedSource>) {
        var feedCheckInString = ""
        feedsChecked.forEach { if (it.lastChecked) feedCheckInString += "- ${it.name}\n" }

        if (feedCheckInString.isEmpty()) {
            feedSource?.textColor = ContextCompat.getColor(context, R.color.feedSources_color)
            feedSource?.text = context.getString(R.string.feed_source_label)
        } else {
            feedSource?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
            feedSource?.text = feedCheckInString
        }
    }

    override fun prepareRefreshInterval(position: Int) {
        val spinner = refreshIntervalView.refreshInterval
        spinner.init(R.array.refresh_interval_array)
        refreshIntervalView.refreshInterval.setSelection(position)
        feedRefreshInterval?.text = spinner.selectedItem.toString()
    }

    override fun prepareFeedLocation(feedLocation: FeedLocation) {
        if (feedLocation.location.isNotBlank()) {
            feedViewLocation?.textColor = ContextCompat.getColor(context, R.color.umbrella_green)
            feedLocationView.autocompleteLocation.setText(feedLocation.location)
            feedViewLocation?.text = feedLocation.location
            feedLocationView.autocompleteLocation.setSelection(feedLocation.location.length)
        }
    }

    override fun prepareFeedSource(feedSources: List<FeedSource>) {
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

    override fun newAddressAvailable(locationInfo: LocationInfo) {
        if (locationInfo.locationNames.isNotEmpty())
            feedLocationAutoText.updateAddress(locationInfo)
    }

    private fun refreshIntervalCancel() = refreshIntervalAlertDialog.dismiss()

    private fun feedSourceCancel() = feedSourceAlertDialog.dismiss()

    private fun feedLocationCancel() = feedLocationAlertDialog.dismiss()
}