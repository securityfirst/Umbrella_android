package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*

import kotlinx.android.synthetic.main.feed_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.misc.init

class FeedController : BaseController() {

    private lateinit var undefinedFeedView: View
    private lateinit var refreshIntervalView: View
    private lateinit var feedLocationView: View
    private lateinit var undefinedFeedAlertDialog: AlertDialog
    private lateinit var refreshIntervalAlertDialog: AlertDialog
    private lateinit var feedLocationAlertDialog: AlertDialog


    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)

        setUndefinedFeed?.let { linear -> linear.setOnClickListener { onClickUndefinedFeed() } }
        setRefreshInterval?.let { linear -> linear.setOnClickListener { onClickRefreshInterval() } }
        setLocation?.let { linear -> linear.setOnClickListener { onClickFeedLocation() } }
        setFeedSource?.let { linear -> linear.setOnClickListener { onClickFeedSource() } }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {

        undefinedFeedView = inflater.inflate(R.layout.feed_source_dialog, container, false)
        refreshIntervalView = inflater.inflate(R.layout.feed_interval_dialog, container, false)
        feedLocationView = inflater.inflate(R.layout.feed_location_dialog, container, false)

        refreshIntervalView.refreshInterval.init(R.array.refresh_interval_array)

        undefinedFeedAlertDialog = AlertDialog
                .Builder(activity)
                .setView(undefinedFeedView)
                .create()
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
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return undefinedFeedAlertDialog
            }
        })
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
                return undefinedFeedAlertDialog
            }
        })
    }
}