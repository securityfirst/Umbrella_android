package org.secfirst.umbrella.whitelabel.feature.difficulty.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.difficulty_item_view.view.*
import org.jetbrains.anko.backgroundColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty

class DifficultyAdapter(private val onClickDiff: (Int) -> Unit) : RecyclerView.Adapter<DifficultyAdapter.DifficultHolder>() {

    private val difficulties = mutableListOf<Difficulty>()

    fun clear() = difficulties.clear()

    fun getItem(position: Int) = difficulties[position]

    fun addAll(difficulties: List<Difficulty>) {
        this.difficulties.addAll(difficulties)
        notifyDataSetChanged()
    }

    override fun getItemCount() = difficulties.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DifficultHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.difficulty_item_view, parent, false)
        return DifficultHolder(view)
    }

    override fun onBindViewHolder(holder: DifficultHolder, position: Int) {
        holder.bind(difficulties[position], clickListener = { onClickDiff(position) })
    }

    fun getItems(position: Int): List<Difficulty> {
        val sortItems = mutableListOf<Difficulty>()
        sortItems.add(difficulties[position])
        difficulties.forEach {
            if (difficulties[position].id != it.id)
                sortItems.add(it)
        }
        return sortItems

    }

    class DifficultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("Range")
        fun bind(difficulty: Difficulty, clickListener: (DifficultHolder) -> Unit) {
            itemView.setOnClickListener { clickListener(this) }
            with(difficulty) {
                itemView.difficultTitle.text = title
                itemView.difficultDescription.text = description
                itemView.difficultLayout.backgroundColor = Color.parseColor(layoutColor)
            }
        }
    }
}