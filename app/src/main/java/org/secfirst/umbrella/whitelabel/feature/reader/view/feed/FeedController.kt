package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*
import kotlinx.android.synthetic.main.feed_view.*
import org.jetbrains.anko.AnkoContext
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
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
        presenter.submitLoadFeedSources()
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)
        feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)
        refreshIntervalView.refreshInterval.init(R.array.refresh_interval_array)
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

    private fun feedSourceCancel() {
        feedSourceAlertDialog.dismiss()
    }

    private fun populateFeedSource(feedsChecked: List<FeedSource>) {
        var feedCheckInString = ""
        feedsChecked.forEach { feedChecked ->
            if (feedChecked.lastChecked)
                feedCheckInString += "- ${feedChecked.name}\n"
        }
        feedSource?.text = feedCheckInString
    }


    override fun prepareFeedSource(feedSources: List<FeedSource>) {
        populateFeedSource(feedSources)
        feedSourceDialog = FeedSourceDialog(feedSources)
        val feedSourceView = feedSourceDialog.createView(AnkoContext.create(context, this, false))
        feedSourceAlertDialog = AlertDialog
                .Builder(activity)
                .setView(feedSourceView)
                .create()
        feedSourceView.alertControlOk.setOnClickListener { feedSourceOK() }
        feedSourceView.alertControlCancel.setOnClickListener { feedSourceCancel() }
        setFeedSource?.setOnClickListener { onClickFeedSource() }

    }
}