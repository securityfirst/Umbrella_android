package org.secfirst.umbrella.data.preferences

import android.content.Context
import android.content.SharedPreferences
import org.secfirst.umbrella.data.disk.IsoCountry
import org.secfirst.umbrella.di.PreferenceInfo
import javax.inject.Inject

class AppPreferenceHelper @Inject constructor(context: Context, @PreferenceInfo private val prefFileName: String) : PreferenceHelper {

    private val prefs: SharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    companion object {
        private const val EXTRA_REFRESH_INTERVAL = "refresh_interval"
        private const val EXTRA_SKIP_PASSWORD = "skip_password"
        private const val UPDATE_CONTENT = "update_content"
        private const val STATE_PW_BANNER = "pw_banner"
        const val EXTRA_MASK_APP = "mask_app"
        const val PREF_NAME = "umbrella.preference"
        const val EXTRA_LOGGED_IN = "is_logged_in"
        const val EXTRA_LANGUAGE = "language"
        const val EXTRA_SHOW_MOCK_VIEW = "extra_show_mock_view"
        const val SKIP_PATHWAYS = "skip_pathways"
    }

    override fun setMockView(result: Boolean) = prefs.edit().putBoolean(EXTRA_SHOW_MOCK_VIEW, result).commit()

    override fun isShowMockView(): Boolean = prefs.getBoolean(EXTRA_SHOW_MOCK_VIEW, false)

    override fun setLanguage(isoCountry: String) = prefs.edit().putString(EXTRA_LANGUAGE, isoCountry).commit()

    override fun getLanguage() = prefs.getString(EXTRA_LANGUAGE, "") ?: IsoCountry.ENGLISH.value

    override fun enablePasswordBanner(enableBanner: Boolean) = prefs.edit().putBoolean(STATE_PW_BANNER, enableBanner).commit()

    override fun isPasswordBanner() = prefs.getBoolean(STATE_PW_BANNER, false)

    override fun cleanPreferences() = prefs.edit().clear().commit()

    override fun setShaId(shaID: String) = prefs.edit().putString(UPDATE_CONTENT, shaID).commit()

    override fun getShaId() = prefs.getString(UPDATE_CONTENT, "") ?: ""

    override fun isMaskApp() = prefs.getBoolean(EXTRA_MASK_APP, false)

    override fun setMaskApp(isMaskApp: Boolean) = prefs.edit().putBoolean(EXTRA_MASK_APP, isMaskApp).commit()

    override fun isLoggedIn() = prefs.getBoolean(EXTRA_LOGGED_IN, false)

    override fun setLoggedIn(isLoggedIn: Boolean) = prefs.edit().putBoolean(EXTRA_LOGGED_IN, isLoggedIn).commit()

    override fun setSkipPassword(isSkip: Boolean) = prefs.edit().putBoolean(EXTRA_SKIP_PASSWORD, isSkip).commit()

    override fun getSkipPassword() = prefs.getBoolean(EXTRA_SKIP_PASSWORD, false)

    override fun setRefreshInterval(position: Int) = prefs.edit().putInt(EXTRA_REFRESH_INTERVAL, position).commit()

    override fun getRefreshInterval() = prefs.getInt(EXTRA_REFRESH_INTERVAL, 30)

    override fun setSkipPathways(skip: Boolean) = prefs.edit().putBoolean(SKIP_PATHWAYS, skip).commit()

    override fun getSkipPathways() = prefs.getBoolean(SKIP_PATHWAYS, false)

}

interface PreferenceHelper {

    fun isLoggedIn(): Boolean

    fun setLoggedIn(isLoggedIn: Boolean): Boolean

    fun isMaskApp(): Boolean

    fun setMaskApp(isMaskApp: Boolean): Boolean

    fun setMockView(result: Boolean): Boolean

    fun isShowMockView(): Boolean

    fun setSkipPassword(isSkip: Boolean): Boolean

    fun getSkipPassword(): Boolean

    fun setRefreshInterval(position: Int): Boolean

    fun getRefreshInterval(): Int

    fun setShaId(shaID: String): Boolean

    fun getShaId(): String

    fun setLanguage(isoCountry: String): Boolean

    fun getLanguage(): String

    fun cleanPreferences(): Boolean

    fun isPasswordBanner(): Boolean

    fun enablePasswordBanner(enableBanner: Boolean): Boolean

    fun setSkipPathways(skip: Boolean): Boolean

    fun getSkipPathways(): Boolean
}