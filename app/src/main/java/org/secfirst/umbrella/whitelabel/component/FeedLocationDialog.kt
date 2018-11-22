package org.secfirst.umbrella.whitelabel.component

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.location.Geocoder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.feed_location_dialog.view.*
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent

class FeedLocationDialog(private val feedLocationView: View,
                         private val controller: BaseController,
                         private val feedLocationListener: FeedLocationListener) {

    private lateinit var locationInfo: LocationInfo
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
        val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, listOf())
        feedLocationView.autocompleteLocation.autocompleteLocation.threshold = 2
        feedLocationView.autocompleteLocation.autocompleteLocation.setAdapter(adapter)
        feedLocationView.autocompleteLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                prepareAutocomplete(s.toString())
            }
        })
    }

    fun startLocationView() {
        val dialogManager = DialogManager(controller)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return feedLocationAlertDialog
            }
        })
    }

    private fun feedLocationCancel() {
        feedLocationAlertDialog.dismiss()
        feedLocationListener.onLocationCancel()
    }

    private fun feedLocationOk() {
        val newLocation = feedLocationView.autocompleteLocation.text.toString()
        if (newLocation.isNotBlank()) {
            val feedLocation = FeedLocation(1, newLocation, locationInfo.countryCode)
            feedLocationListener.onLocationSuccess(feedLocation)
        }
        feedLocationAlertDialog.dismiss()
    }

    private fun updateAddress(locationInfo: LocationInfo) {
        this.locationInfo = locationInfo
        val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, locationInfo.locationNames)
        feedLocationView.autocompleteLocation.setAdapter(adapter)
    }


    private fun prepareAutocomplete(characters: String) {
        launchSilent(uiContext) {
            locationInfo = getAddress(characters)
            if (locationInfo.locationNames.isNotEmpty())
                updateAddress(locationInfo)
        }
    }

    private suspend fun getAddress(locationName: String): LocationInfo {
        val geocoder = Geocoder(context)
        val addressLabelList = mutableListOf<String>()
        var countryCode = ""
        var locationInfo = LocationInfo()
        withContext(ioContext) {
            if (locationName.isNotBlank()) {
                try {
                    val addressList = geocoder.getFromLocationName(locationName, 5)
                    addressList.forEach { fullAddress ->
                        countryCode = fullAddress.countryCode ?: ""
                        addressLabelList.add(fullAddress.getAddressLine(0))
                    }
                    locationInfo = LocationInfo(addressLabelList, countryCode)
                } catch (e: Exception) {
                    Log.e("prepareAutocomplete", "geolocation error.")
                }
            }
        }
        return locationInfo
    }

    interface FeedLocationListener {
        fun onLocationSuccess(feedLocation: FeedLocation)
        fun onLocationCancel() {}
    }
}