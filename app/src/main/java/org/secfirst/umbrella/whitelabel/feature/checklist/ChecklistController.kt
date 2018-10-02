package org.secfirst.umbrella.whitelabel.feature.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class ChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    private lateinit var checklistView: View
    var titleTab = ""

    private val checklist by lazy { args.getParcelable(EXTRA_CHECKLIST) as Checklist }

    constructor(checklist: Checklist) : this(Bundle().apply {
        putParcelable(EXTRA_CHECKLIST, checklist)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        checklistView = inflater.inflate(R.layout.segment_view, container, false)
        return checklistView
    }

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    companion object {
        const val EXTRA_CHECKLIST = "extra_checklist"
    }
}