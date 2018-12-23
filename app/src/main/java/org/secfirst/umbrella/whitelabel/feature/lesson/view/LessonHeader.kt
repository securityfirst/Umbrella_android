package org.secfirst.umbrella.whitelabel.feature.lesson.view

import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.lesson_menu_head.*
import kotlinx.android.synthetic.main.lesson_menu_head.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import java.io.File

open class LessonHeader(private val moduleId: String,
                   private val iconPath: String,
                   private val titleHeader: String) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.subHeaderText.text = titleHeader

        if (moduleId == Markdown.FAVORITE_INDEX)
            Picasso.with(viewHolder.iconHeader.context)
                    .load(R.drawable.ic_fav)
                    .into(viewHolder.iconHeader.iconHeader)
        else
            Picasso.with(viewHolder.iconHeader.context)
                    .load(File(iconPath))
                    .into(viewHolder.iconHeader.iconHeader)
    }

    override fun getLayout() = R.layout.lesson_menu_head

}