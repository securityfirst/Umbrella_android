package org.secfirst.umbrella.whitelabel.misc

import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


val currentTime: String
    get() {
        val dateFormat = SimpleDateFormat("dd/M/yyyy hh:mm", Locale.ENGLISH)
        return dateFormat.format(Date())
    }


fun convertDateToString(date: Date?): String {
    val dateFormat: DateFormat
    var dateConvert = ""
    try {
        if (date != null) {
            dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH)
            dateConvert = dateFormat.format(date)
        }
    } catch (e: Exception) {
        return dateConvert
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

