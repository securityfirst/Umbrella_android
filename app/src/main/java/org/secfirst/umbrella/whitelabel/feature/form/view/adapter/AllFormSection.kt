package org.secfirst.umbrella.whitelabel.feature.form.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import kotlinx.android.synthetic.main.all_form_item_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.misc.HeaderViewHolder

class AllFormSection(private val onItemClick: (Form) -> Unit,
                     private val titleSection: String,
                     private val forms: MutableList<Form>) : StatelessSection(SectionParameters.builder()
        .itemResourceId(R.layout.all_form_item_view)
        .headerResourceId(R.layout.head_section)
        .build()) {

    override fun getContentItemsTotal() = forms.size
    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val formHolder = holder as ItemAllFormHolder
        formHolder.bind(forms[position].title, clickListener = { onItemClick(forms[position]) })
    }

    override fun getItemViewHolder(view: View?) = ItemAllFormHolder(view)

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val headerHolder = holder as HeaderViewHolder?
        headerHolder?.let { it.sectionText.text = titleSection }
    }

}

class ItemAllFormHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    private val titleForm = itemView?.titleAllForm
    fun bind(title: String, clickListener: (ItemAllFormHolder) -> Unit) {
        titleForm?.let { it.text = title }
        itemView.setOnClickListener { clickListener(this) }
    }
}

