package org.secfirst.umbrella.whitelabel.deeplink

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.airbnb.deeplinkdispatch.DeepLinkHandler

@DeepLinkHandler(AppDeepLinkModule::class)
class DeepLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deepLinkDelegate = DeepLinkDelegate(AppDeepLinkModuleLoader())
        deepLinkDelegate.dispatchFrom(this)
        finish()
    }
}