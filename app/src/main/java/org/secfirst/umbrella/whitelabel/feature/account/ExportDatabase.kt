package org.secfirst.umbrella.whitelabel.feature.account

import android.view.View
import kotlinx.android.synthetic.main.settings_export_dialog.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity


class ExportDatabase(private val view: View, private val mainActivity: MainActivity) {




    init {
        view.exportDialogOk.onClick {  }
    }



    private fun initExportOptions() {
//        mCancel.setOnClickListener(this)
//        mOk.setOnClickListener(this)
//        view.ExportDialogWipeData.setOnClickListener {  }
    }

}