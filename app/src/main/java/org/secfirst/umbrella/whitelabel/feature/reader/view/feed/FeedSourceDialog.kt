package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.app.AlertDialog
import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.alert_control.view.*
import org.jetbrains.anko.AnkoContext
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource

class FeedSourceDialog(feedSources: List<FeedSource>,
                       context: Context,
                       private val listener: FeedSourceListener) {

    private var feedSourceAlertDialog: AlertDialog
    private var feedSourceUI = FeedSourceUI(feedSources)
    private var feedSourceView: View

    init {
        feedSourceView = feedSourceUI.createView(AnkoContext.create(context, this, false))
        feedSourceAlertDialog = AlertDialog
                .Builder(context)
                .setView(feedSourceView)
                .create()
        initView()
    }

    fun show() {
        feedSourceAlertDialog.show()
    }

    private fun initView() {
        feedSourceView.alertControlOk.setOnClickListener { feedSourceOK() }
        feedSourceView.alertControlCancel.setOnClickListener { feedSourceCancel() }
    }

    private fun feedSourceCancel() {
        listener.onFeedSourceCancel()
        feedSourceAlertDialog.dismiss()
    }

    private fun feedSourceOK() {
        listener.onFeedSourceSuccess(feedSourceUI.getFeedSourcesUpdated())
        feedSourceAlertDialog.dismiss()
    }

    interface FeedSourceListener {
        fun onFeedSourceSuccess(feedSources: List<FeedSource>)
        fun onFeedSourceCancel() {}
    }
}