package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.content.Context
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.feed_location_dialog.view.*
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView

class FeedLocationAutoText(private val autocompleteLocation: AppCompatAutoCompleteTextView,
                           private val context: Context,
                           private val presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>,
                           private var locationInfo: LocationInfo) {
    init {
        startAutocompleteLocation()
    }

    private fun startAutocompleteLocation() {
        val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, listOf())
        autocompleteLocation.autocompleteLocation.threshold = 2
        autocompleteLocation.autocompleteLocation.setAdapter(adapter)
        autocompleteLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.submitAutocompleteAddress(s.toString())
            }
        })
    }

    fun updateAddress(locationInfo: LocationInfo) {
        this.locationInfo = locationInfo
        val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, locationInfo.locationNames)
        autocompleteLocation.autocompleteLocation.threshold = 2
        autocompleteLocation.setAdapter(adapter)
    }

    fun getCountryCode() = locationInfo.countryCode ?: ""
}