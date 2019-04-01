package org.secfirst.umbrella.whitelabel.feature.tour.view

import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
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

            verticalLayout {
                imageView(imageSource).lparams(width = wrapContent, height =  wrapContent) {
                    gravity = Gravity.CENTER
                    topMargin = dip(20)
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

            val params = LinearLayout.LayoutParams(wrapContent, wrapContent)
            params.bottomMargin = dip(70)
            params.leftMargin = dip(10)
            params.rightMargin = dip(10)
            webView {
                loadUrl("file:///android_asset/terms.html")
                visibility = visibilityWebView
                bottomPadding = dip(20)
            }.lparams(params)
        }
    }.view

    override fun onSelected() {}

    override fun verifyStep(): Nothing? = null

    override fun onError(error: VerificationError) {}
}