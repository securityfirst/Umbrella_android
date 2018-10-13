package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent

class FeedController : BaseController() {

    private lateinit var undefinedFeedView: View

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUndefinedFeed?.let { linear -> linear.setOnClickListener { onClickUndefinedFeed() } }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        undefinedFeedView = inflater.inflate(R.layout.feed_source_dialog, container, false)

        return inflater.inflate(R.layout.feed_view, container, false)
    }

    private fun onClickUndefinedFeed() {
        val dialogManager = DialogManager(this)
        val alertDialog = AlertDialog
                .Builder(activity)
                .setView(undefinedFeedView)
                .create()

        dialogManager.showDialog("undefinedFeed", object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return alertDialog
            }
        })
    }
}