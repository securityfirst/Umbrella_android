package org.secfirst.umbrella.whitelabel.data.preferences

import android.content.Context
import android.content.SharedPreferences
import org.secfirst.umbrella.whitelabel.di.PreferenceInfo
import javax.inject.Inject

class AppPreferenceHelper @Inject constructor(context: Context,
                                              @PreferenceInfo private val prefFileName: String) : PreferenceHelper {

    private val prefs: SharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    companion object {
        private const val EXTRA_REFRESH_INTERVAL = "refresh_interval"
    }

    override fun setRefreshInterval(position: Int) = prefs.edit().putInt(EXTRA_REFRESH_INTERVAL, position).commit()

    override fun getRefreshInterval() = prefs.getInt(EXTRA_REFRESH_INTERVAL, 0)

}

interface PreferenceHelper {

    fun setRefreshInterval(position: Int): Boolean

    fun getRefreshInterval(): Int
}