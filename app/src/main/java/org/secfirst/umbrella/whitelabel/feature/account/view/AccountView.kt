package org.secfirst.umbrella.whitelabel.feature.account.view

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView
import java.io.File

interface AccountView : BaseView {

    fun isUserLogged(res: Boolean){}

    fun isTokenChanged(res: Boolean) {}

    fun exportDatabaseSuccessfully() {}

    fun onShareContent(backupFile: File) {}

    fun onImportBackupSuccess() {}

    fun onImportBackupFail() {}

    fun loadDefaultValue(feedLocation: FeedLocation?, refreshFeedInterval: Int, feedSource: List<FeedSource>) {}

    fun getSkipPassword(res : Boolean) {}
}
