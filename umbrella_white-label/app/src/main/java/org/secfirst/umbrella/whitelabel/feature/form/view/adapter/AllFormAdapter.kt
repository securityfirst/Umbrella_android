package org.secfirst.umbrella.whitelabel.feature.form.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.all_form_item_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.Form


class AllFormAdapter(private val onItemClick: (Form) -> Unit) : RecyclerView.Adapter<FormViewHolder>() {

    private val context = UmbrellaApplication.instance
    private val forms = mutableListOf<Form>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.all_form_item_view, parent, false)

        return FormViewHolder(view)
    }

    fun updateForms(forms: List<Form>) {
        this.forms.clear()
        this.forms.addAll(forms)
        notifyDataSetChanged()
    }

    override fun getItemCount() = forms.size

    override fun onBindViewHolder(formHolder: FormViewHolder, position: Int) {
        formHolder.bind(forms[position].title, clickListener = { onItemClick(forms[it.adapterPosition]) })
    }
}

class FormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleForm = itemView.titleAllForm
    fun bind(title: String, clickListener: (FormViewHolder) -> Unit) {
        titleForm.text = title
        itemView.setOnClickListener { clickListener(this) }
    }
}