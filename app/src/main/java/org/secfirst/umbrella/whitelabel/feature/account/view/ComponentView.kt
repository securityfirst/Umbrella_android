package org.secfirst.umbrella.whitelabel.feature.account.view

import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView
import java.io.File

interface AccountView : BaseView {

    fun isLogged(res: Boolean){}

    fun exportDatabaseSuccessfully(){}

    fun onShareContent(backupFile : File){}

    fun onImportBackupSuccess(){}

    fun onImportBackupFail(){}
}
