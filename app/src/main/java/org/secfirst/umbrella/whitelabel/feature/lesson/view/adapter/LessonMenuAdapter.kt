package org.secfirst.umbrella.whitelabel.feature.lesson.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.lesson_menu_head.view.*
import kotlinx.android.synthetic.main.lesson_menu_item.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import java.io.File


class LessonMenuAdapter(groups: List<ExpandableGroup<*>>,
                        private val onclickLesson: (Lesson.Topic) -> Unit)
    : ExpandableRecyclerViewAdapter<LessonMenuAdapter.HeadHolder, LessonMenuAdapter.LessonMenuHolder>(groups) {


    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): HeadHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.lesson_menu_head, parent, false)
        return HeadHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): LessonMenuHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.lesson_menu_item, parent, false)
        return LessonMenuHolder(view)
    }

    override fun onBindChildViewHolder(holder: LessonMenuHolder, flatPosition: Int, group: ExpandableGroup<*>, childIndex: Int) {
        val itemGroup = (group as Lesson).items[childIndex]
        holder.bind(itemGroup.title, clickListener = { onclickLesson(itemGroup) })
    }

    override fun onBindGroupViewHolder(holder: HeadHolder, flatPosition: Int, group: ExpandableGroup<*>) {
        holder.setHeadTitle(group)
    }

    class HeadHolder(itemView: View) : GroupViewHolder(itemView) {
        fun setHeadTitle(group: ExpandableGroup<*>) {
            val itemSection = group as Lesson
            itemView.subheaderText.text = group.title
            Picasso.with(itemView.context)
                    .load(File(itemSection.pathIcon))
                    .into(itemView.iconHeader)
        }
    }

    class LessonMenuHolder(itemView: View) : ChildViewHolder(itemView) {
        fun bind(titleSection: String, clickListener: (LessonMenuHolder) -> Unit) {
            itemView.categoryName.text = titleSection
            itemView.setOnClickListener { clickListener(this) }
        }
    }
}