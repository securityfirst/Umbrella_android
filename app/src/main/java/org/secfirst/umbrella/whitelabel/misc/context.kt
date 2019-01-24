package org.secfirst.umbrella.whitelabel.misc

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.view.WindowManager

fun Context.getDrawableCompat(resId: Int): Drawable {
    return AppCompatResources.getDrawable(this, resId)!!
}

fun Context.getColorCompat(id: Int): Int {
    return ContextCompat.getColor(this, id)
}


fun Context.dpToPx(dp: Float): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun Context.spToPx(sp: Float): Float {
    val scale = resources.displayMetrics.scaledDensity
    return sp * scale
}

fun Context.getWindowDimensions(): Point {
    val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

fun Context.getWindowWidth(): Int {
    return getWindowDimensions().x
}

fun Context.getWindowHeight(): Int {
    return getWindowDimensions().y
}