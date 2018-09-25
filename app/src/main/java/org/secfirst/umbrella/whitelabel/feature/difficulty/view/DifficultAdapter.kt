package org.secfirst.umbrella.whitelabel.feature.difficulty.view

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.difficult_item_view.view.*
import org.jetbrains.anko.backgroundColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty

class DifficultAdapter(private val onClickDiff: (Difficulty.Item) -> Unit) : RecyclerView.Adapter<DifficultAdapter.DifficultHolder>() {

    private val difficulties = mutableListOf<Difficulty.Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DifficultHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.difficult_item_view, parent, false)
        return DifficultHolder(view)
    }


    override fun onBindViewHolder(holder: DifficultHolder, position: Int) {
        holder.bind(difficulties[position], clickListener = { onClickDiff(difficulties[position]) })
    }

    override fun getItemCount() = difficulties.size

    fun addAll(difficulties: List<Difficulty.Item>) {
        difficulties.forEach { this.difficulties.add(it) }
        notifyDataSetChanged()
    }

    class DifficultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(difficulty: Difficulty.Item, clickListener: (DifficultHolder) -> Unit) {
            itemView.setOnClickListener { clickListener(this) }
            with(difficulty) {
                itemView.difficultTitle.text = title
                itemView.difficultDescription.text = description
                itemView.difficultLayout.backgroundColor = Color.parseColor(layoutColor)
            }
        }
    }
}