package org.secfirst.umbrella.whitelabel.misc

import android.graphics.Typeface
import android.widget.TextView

val TextView.regular: Typeface get() = Typeface.createFromAsset(context.assets, "fonts/Roboto-Regular.ttf")
val TextView.medium: Typeface get() = Typeface.createFromAsset(context.assets, "fonts/Roboto-Medium.ttf")