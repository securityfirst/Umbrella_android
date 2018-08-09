package org.secfirst.umbrella.whitelabel.feature.form.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.truizlop.sectionedrecyclerview.SimpleSectionedAdapter

import org.secfirst.umbrella.whitelabel.R.layout.section



class Test : SimpleSectionedAdapter<CountItemViewHolder>() {

    override fun getSectionHeaderTitle(section: Int) = if (section == 0) "Today" else "Tomorrow"

    override fun isSectionHeaderViewType(viewType: Int): Boolean {
        return super.isSectionHeaderViewType(viewType)
    }

    override fun getSectionCount() = 2

    override fun onCreateItemViewHolder(parent: ViewGroup?, viewType: Int): CountItemViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCountForSection(section: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindItemViewHolder(holder: CountItemViewHolder?, section: Int, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


private class ActiveFormViewHolderr(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleForm = itemView.titleActiveForm
    private val currentTime = itemView.currentTime
    private val edit = itemView.editForm
    private val share = itemView.shareForm
    private val delete = itemView.deleteForm

    fun bind(title: String, timeNow: String,
             editClickListener: (ActiveFormViewHolder) -> Unit,
             deleteClickListener: (ActiveFormViewHolder) -> Unit,
             shareClickListener: (ActiveFormViewHolder) -> Unit) {

        titleForm.text = title
        currentTime.text = timeNow

        edit.setOnClickListener { editClickListener(this) }
        share.setOnClickListener { shareClickListener(this) }
        delete.setOnClickListener { deleteClickListener(this) }
    }
}