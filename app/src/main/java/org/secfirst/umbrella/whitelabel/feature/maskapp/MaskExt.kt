package org.secfirst.umbrella.whitelabel.feature.maskapp

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Handler
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.EXTRA_MASK_APP
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.PREF_NAME

const val EXTRA_SHAKE_DEVICE = "extra_shake_device"

fun Context.isMaskMode(): Boolean {
    val shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    return shared.getBoolean(EXTRA_MASK_APP, false)
}

fun Context.isNotMaskMode(): Boolean {
    val shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    return !shared.getBoolean(EXTRA_MASK_APP, false)
}

fun Context.isNotShakeDevice(): Boolean {
    val shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    return !shared.getBoolean(EXTRA_SHAKE_DEVICE, false)
}

fun Context.setMaskModeDelayed(delayMillis: Long) {
    val shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    shared.edit().putBoolean(EXTRA_MASK_APP, false).apply()
    Handler().postDelayed({ shared.edit().putBoolean(EXTRA_MASK_APP, true).apply() }, delayMillis)
}

fun Context.setShakeDeviceDelayed(delayMillis: Long) {
    val shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    shared.edit().putBoolean(EXTRA_SHAKE_DEVICE, true).apply()
    Handler().postDelayed({ shared.edit().putBoolean(EXTRA_SHAKE_DEVICE, false).apply() }, delayMillis)
}

fun Context.setMaskApp(isMaskApp: Boolean) {
    val shared = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    shared.edit().putBoolean(EXTRA_MASK_APP, isMaskApp).apply()
}