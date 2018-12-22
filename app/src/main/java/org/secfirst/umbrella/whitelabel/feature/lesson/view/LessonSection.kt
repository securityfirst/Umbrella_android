package org.secfirst.umbrella.whitelabel.feature.lesson.view

import android.support.v7.widget.RecyclerView
import android.view.View
import com.squareup.picasso.Picasso
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import kotlinx.android.synthetic.main.lesson_menu_head.view.*
import kotlinx.android.synthetic.main.lesson_menu_item.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import java.io.File

class LessonSection(private val lesson: Lesson, private val onItemClick: (Subject) -> Unit,
                    private val onHeaderClick: (String) -> Unit) : StatelessSection(SectionParameters.builder()
        .itemResourceId(R.layout.lesson_menu_item)
        .headerResourceId(R.layout.lesson_menu_head)
        .build()) {

    private var expanded = true

    override fun getContentItemsTotal(): Int {
        return if (expanded) lesson.topics.size else 0
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as ItemHolder
        val currentTopic = lesson.topics[position]
        holder.bind(currentTopic.title, clickListener = { onItemClick(currentTopic) })
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        holder as HeaderHolder
        holder.itemView.arrow.setOnClickListener {
            expanded = !expanded
            holder.itemView.arrow.setImageResource(
                    if (expanded) R.drawable.ic_keyboard_arrow_up_black else R.drawable.ic_keyboard_arrow_down_black)

        }
        holder.bind(lesson)
    }

    override fun getItemViewHolder(view: View) = ItemHolder(view)

    override fun getHeaderViewHolder(view: View) = HeaderHolder(view)

    class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(lesson: Lesson) {
            itemView.subheaderText.text = lesson.moduleTitle
            if (lesson.moduleId == Markdown.FAVORITE_INDEX)
                Picasso.with(itemView.context)
                        .load(R.drawable.ic_fav)
                        .into(itemView.iconHeader)
            else
                Picasso.with(itemView.context)
                        .load(File(lesson.pathIcon))
                        .into(itemView.iconHeader)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(titleSection: String, clickListener: (ItemHolder) -> Unit) {
            itemView.categoryName.text = titleSection
            itemView.setOnClickListener { clickListener(this) }
        }
    }
}