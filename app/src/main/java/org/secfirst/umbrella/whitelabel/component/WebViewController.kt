package org.secfirst.umbrella.whitelabel.component

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.web_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class WebViewController(bundle: Bundle) : BaseController(bundle) {

    private val url by lazy { args.getString(EXTRA_WEB_VIEW_URL) }
    private var refreshEnable: Boolean = false

    companion object {
        const val EXTRA_WEB_VIEW_URL = "url"
    }

    constructor(url: String) : this(Bundle().apply {
        putString(EXTRA_WEB_VIEW_URL, url)
    })

    override fun onInject() {
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpWebView()
        enableToolbar()
        enableArrowBack(true)
        disableNavigation()
    }

    override fun onDestroyView(view: View) {
        enableNavigation()
        super.onDestroyView(view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.web_view, container, false)
    }

    private fun setUpWebView() {
        webView?.let {
            it.loadUrl(url)
            it.webViewClient = object : WebViewClient() {
                override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                    webViewLoad?.visibility = INVISIBLE
                    webViewSwipe?.isEnabled = false
                }

                @TargetApi(android.os.Build.VERSION_CODES.M)
                override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
                    onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
                    webViewLoad?.visibility = INVISIBLE
                    webViewSwipe?.isEnabled = false
                    if (refreshEnable) webViewSwipe?.isRefreshing = false
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    if (!refreshEnable) webViewLoad?.visibility = VISIBLE
                    Handler().postDelayed({
                        webViewLoad?.visibility = INVISIBLE
                        webViewSwipe?.isRefreshing = false
                    }, 30000)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    webViewLoad?.visibility = INVISIBLE
                    if (refreshEnable) webViewSwipe?.isRefreshing = false

                }
            }
        }

    }

    override fun getEnableBackAction() = true

    override fun getToolbarTitle() = "Umbrella"

}