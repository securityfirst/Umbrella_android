package org.secfirst.umbrella.component

import android.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_interval_dialog.view.*
import org.secfirst.umbrella.R

class RefreshIntervalDialog(private val view: View, private val initialInterval: String,
                            private val listener: RefreshIntervalListener) {

    private val refreshIntervalDialog = AlertDialog
            .Builder(view.context)
            .setView(view)
            .create()


    init {
        view.alertControlOk.setOnClickListener { refreshIntervalOk() }
        view.alertControlCancel.setOnClickListener { refreshIntervalCancel() }
    }

    fun show() {
        refreshIntervalDialog.show()
    }

    private fun refreshIntervalOk() {
        val selectedInterval = view.feedInterval.text.toString()
        refreshIntervalDialog.dismiss()
        listener.onRefreshIntervalSuccess(selectedInterval)
    }

    private fun refreshIntervalCancel() {
        listener.onRefreshIntervalCancel()
        refreshIntervalDialog.dismiss()
    }

    interface RefreshIntervalListener {
        fun onRefreshIntervalSuccess(selectedInterval: String)
        fun onRefreshIntervalCancel() {}
    }
}