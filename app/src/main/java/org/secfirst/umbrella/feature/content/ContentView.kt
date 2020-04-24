package org.secfirst.umbrella.feature.content

import org.secfirst.umbrella.feature.base.view.BaseView


interface ContentView : BaseView {

    fun downloadContentCompleted(res: Boolean){}

    fun downloadContentInProgress() {}

    fun onCleanDatabaseSuccess() {}
}