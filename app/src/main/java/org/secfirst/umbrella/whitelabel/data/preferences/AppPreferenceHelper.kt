package org.secfirst.umbrella.whitelabel.data.preferences

import android.content.Context
import android.content.SharedPreferences
import org.secfirst.umbrella.whitelabel.di.PreferenceInfo
import javax.inject.Inject

class AppPreferenceHelper @Inject constructor(context: Context,
                                              @PreferenceInfo private val prefFileName: String) : PreferenceHelper {

    private val mPrefs: SharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_KEY_TOPIC_SELECTED = "PREF_KEY_TOPIC_SELECTED"
    }

    override fun getTopicSelected() = mPrefs.getLong(PREF_KEY_TOPIC_SELECTED, 0)

    override fun setTopicSelected(subcategoryId: Long) = mPrefs.edit().putLong(PREF_KEY_TOPIC_SELECTED, subcategoryId).commit()


}

interface PreferenceHelper {

    fun getTopicSelected(): Long

    fun setTopicSelected(subcategoryId: Long): Boolean
}