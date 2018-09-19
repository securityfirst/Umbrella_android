package org.secfirst.umbrella.whitelabel.feature.form.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import kotlinx.android.synthetic.main.active_form_item_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.misc.HeaderViewHolder


class ActiveFormSection(private val onEditItemClick: (ActiveForm) -> Unit,
                        private val onDeleteItemClick: (Int, ActiveForm) -> Unit,
                        private val onShareItemClick: (ActiveForm) -> Unit,
                        private val titleSection: String,
                        private val activeForms: MutableList<ActiveForm>) : StatelessSection(SectionParameters.builder()
        .itemResourceId(R.layout.active_form_item_view)
        .headerResourceId(R.layout.head_section)
        .footerResourceId(R.layout.foot_section)
        .build()) {

    override fun getContentItemsTotal() = activeForms.size

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val activeFormHolder = holder as ItemActiveFormHolder
        val currentTime = activeForms[position].date
        val title = activeForms[position].title
        activeFormHolder.bind(title, currentTime, editClickListener = { onEditItemClick(activeForms[position]) },
                shareClickListener = { onShareItemClick(activeForms[position]) },
                deleteClickListener = { onDeleteItemClick(position, activeForms[position]) })

    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        super.onBindHeaderViewHolder(holder)
        val headerHolder = holder as HeaderViewHolder?
        headerHolder?.let { it.sectionText.text = titleSection }
    }

    override fun getHeaderViewHolder(view: View) = HeaderViewHolder(view)

    override fun getFooterViewHolder(view: View?) = FootViewHolder(view)

    override fun getItemViewHolder(view: View?) = ItemActiveFormHolder(view)

    fun remove(position: Int, sectionAdapter: SectionedRecyclerViewAdapter) {
        sectionAdapter.notifyItemRemoved(position)
        activeForms.removeAt(position)
        sectionAdapter.notifyDataSetChanged()
    }
}


class FootViewHolder(footView: View?) : RecyclerView.ViewHolder(footView)

class ItemActiveFormHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    private val titleForm = itemView?.titleActiveForm
    private val currentTime = itemView?.currentTime
    private val edit = itemView?.editForm
    private val share = itemView?.shareForm
    private val delete = itemView?.deleteForm

    fun bind(title: String, timeNow: String,
             editClickListener: (ItemActiveFormHolder) -> Unit,
             deleteClickListener: (ItemActiveFormHolder) -> Unit,
             shareClickListener: (ItemActiveFormHolder) -> Unit) {

        titleForm?.let { it.text = title }
        currentTime?.let { it.text = timeNow }
        edit?.let { it -> it.setOnClickListener { editClickListener(this) } }
        share?.let { it -> it.setOnClickListener { shareClickListener(this) } }
        delete?.let { it -> it.setOnClickListener { deleteClickListener(this) } }
    }
}

