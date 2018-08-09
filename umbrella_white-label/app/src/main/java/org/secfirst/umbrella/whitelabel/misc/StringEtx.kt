package org.secfirst.umbrella.whitelabel.misc

import java.text.SimpleDateFormat
import java.util.*


val currentTime: String
    get() {
        val dateFormat = SimpleDateFormat("dd/M/yyyy hh:mm", Locale.ENGLISH)
        return dateFormat.format(Date())
    }