package org.secfirst.umbrella.whitelabel.feature.content

import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView


interface ContentView : BaseView {

    fun downloadContentCompleted(res: Boolean)

    fun downloadContentInProgress()

    fun onCleanDatabaseSuccess() {}
}