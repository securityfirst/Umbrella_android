package org.secfirst.umbrella.feature.reader.view.feed

import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_location_dialog.view.*
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.misc.countryList
import org.secfirst.umbrella.misc.countryNames

class FeedLocationDialog(private val feedLocationView: View,
                         private val feedLocationListener: FeedLocationListener) {

    private val context = feedLocationView.context
    private val feedLocationAlertDialog: AlertDialog = AlertDialog
            .Builder(context)
            .setView(feedLocationView)
            .create()

    init {
        initComponent()
        startAutocompleteLocation()
    }

    private fun initComponent() {
        feedLocationView.alertControlCancel.setOnClickListener { feedLocationCancel() }
        feedLocationView.alertControlOk.setOnClickListener { feedLocationOk() }
    }

    private fun startAutocompleteLocation() {
        val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, countryNames())
        feedLocationView.location.threshold = 2
        feedLocationView.location.setAdapter(adapter)
        feedLocationView.location.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun show() {
        feedLocationAlertDialog.show()
    }

    private fun feedLocationCancel() {
        feedLocationAlertDialog.dismiss()
        feedLocationListener.onLocationCancel()
    }

    private fun feedLocationOk() {
        val newLocation = feedLocationView.location.text.toString()
        val country = countryList().find { it.name == newLocation }
        if (newLocation.isNotBlank() && country != null ) {
            val feedLocation = FeedLocation(0, newLocation, country.codeAlpha2)
            feedLocationListener.onLocationSuccess(feedLocation)
        }
        feedLocationAlertDialog.dismiss()
    }

    interface FeedLocationListener {
        fun onLocationSuccess(feedLocation: FeedLocation)
        fun onLocationCancel() {}
    }
}