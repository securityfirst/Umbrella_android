package org.secfirst.umbrella.whitelabel.feature.tour.view

import android.graphics.Color
import android.view.Gravity
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import org.jetbrains.anko.*
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController


class TourUI(private val color: Int,
             private val imageSource: Int,
             private val textTourSource: Int,
             private val visibilityImage: Int,
             private val visibilityWebView: Int) : AnkoComponent<BaseController>, Step {

    override fun createView(ui: AnkoContext<BaseController>) = ui.apply {

        verticalLayout {
            backgroundColorResource = color
            padding = dip(20)

            verticalLayout {
                imageView(imageSource).lparams(width = dip(166), height = dip(166)) {
                    gravity = Gravity.CENTER
                    topMargin = dip(100)
                }
                visibility = visibilityImage
            }

            textView {
                textResource = textTourSource
                textSize = 24f
                textColor = Color.WHITE
                gravity = Gravity.CENTER
                padding = dip(20)
            }.lparams { gravity = Gravity.CENTER }

            webView {
                loadUrl("file:///android_asset/terms.html")
                visibility = visibilityWebView
            }.lparams(width = matchParent, height = dip(460))
        }
    }.view

    override fun onSelected() {}

    override fun verifyStep(): Nothing? = null

    override fun onError(error: VerificationError) {}
}