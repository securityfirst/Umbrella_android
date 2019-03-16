package org.secfirst.umbrella.whitelabel.misc

import android.content.Context
import org.jetbrains.anko.toast
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


const val DOWNLOAD_CHANNEL_ID = "dl_channel"
const val DOWNLOAD_CHANNEL_NAME = "default"

val currentTime: String
    get() {
        val dateFormat = SimpleDateFormat("dd/M/yyyy hh:mm", Locale.ENGLISH)
        return dateFormat.format(Date())
    }


fun convertDateToString(date: Date?): String {
    var dateFormat: DateFormat
    var dateConvert = ""
    date?.let {
        dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
        dateConvert = dateFormat.format(date)
    }
    return dateConvert
}

fun String?.hostURL(): String {
    val stringUrl = this ?: ""
    val url = URL(stringUrl)
    return url.host
}

fun Long?.timestampToStringFormat(): String {
    val timestamp = this ?: 0
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            .format(Date(timestamp * 1000))
}

fun String.checkPasswordStrength(context: Context): Boolean {

    if (this.length < 8) {
        context.toast(context.getString(org.secfirst.umbrella.whitelabel.R.string.password_too_short))
        return false
    } else if (!Pattern.compile("\\d").matcher(this).find()) {
        context.toast(context.getString(org.secfirst.umbrella.whitelabel.R.string.password_one_digit))
        return false
    } else if (!Pattern.compile("[A-Z]").matcher(this).find()) {
        context.toast(context.getString(org.secfirst.umbrella.whitelabel.R.string.password_one_capital))
        return false
    } else if (!Pattern.compile("[A-Z]").matcher(this).find()) {
        context.toast(context.getString(org.secfirst.umbrella.whitelabel.R.string.password_one_small))
        return false
    }
    return true
}

fun String.removeSpecialCharacter(): String {
    val regex = Regex("[^A-Za-z0-9 ]")
    return regex.replace(this, " ")
}