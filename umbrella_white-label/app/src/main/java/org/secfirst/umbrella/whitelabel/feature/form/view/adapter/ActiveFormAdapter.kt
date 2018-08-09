package org.secfirst.umbrella.whitelabel.feature.form.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.active_form_item_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.Form


class ActiveFormAdapter(private val onEditItemClick: (Form) -> Unit,
                        private val onDeleteItemClick: (Form) -> Unit,
                        private val onShareItemClick: (Form) -> Unit) : RecyclerView.Adapter<ActiveFormViewHolder>() {

    private val context = UmbrellaApplication.instance
    private val forms = mutableListOf<Form>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveFormViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.active_form_item_view, parent, false)
        return ActiveFormViewHolder(view)
    }

    override fun getItemCount() = forms.size

    fun updateForms(forms: List<Form>) {
        this.forms.clear()
        this.forms.addAll(forms)
        notifyDataSetChanged()
    }

    fun remove(form: Form) {
        this.forms.remove(form)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(formHolder: ActiveFormViewHolder, position: Int) {
        val currentTime = forms[position].date
        val title = forms[position].title
        formHolder.bind(title, currentTime, editClickListener = { onEditItemClick(forms[it.adapterPosition]) },
                shareClickListener = { onShareItemClick(forms[it.adapterPosition]) },
                deleteClickListener = { onDeleteItemClick(forms[it.adapterPosition]) })
    }

}

class ActiveFormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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