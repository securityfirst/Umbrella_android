package org.secfirst.umbrella.feature.form.view.adapter


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import kotlinx.android.synthetic.main.all_form_item_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.misc.HeaderViewHolder

class AllFormSection(private val onItemClick: (Form) -> Unit,
                     private val titleSection: String,
                     private val forms: MutableList<Form>) : Section(SectionParameters.builder()
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

    override fun getItemViewHolder(view: View) = ItemAllFormHolder(view)

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val headerHolder = holder as HeaderViewHolder?
        headerHolder?.let { it.sectionText.text = titleSection }
    }

}

class ItemAllFormHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleForm = itemView.titleAllForm
    fun bind(title: String, clickListener: (ItemAllFormHolder) -> Unit) {
        titleForm?.let { it.text = title }
        itemView.setOnClickListener { clickListener(this) }
    }
}

