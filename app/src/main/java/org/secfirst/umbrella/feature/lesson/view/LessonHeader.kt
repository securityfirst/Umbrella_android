package org.secfirst.umbrella.feature.lesson.view

import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.lesson_menu_head.*
import kotlinx.android.synthetic.main.lesson_menu_head.view.*
import org.jetbrains.anko.backgroundDrawable
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.misc.appContext
import java.io.File

open class LessonHeader(private val moduleId: String,
                        private val iconPath: String,
                        private val titleHeader: String) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.subHeaderText.text = titleHeader

        if (moduleId == Markdown.FAVORITE_INDEX) {
            viewHolder.subHeaderText.text =  appContext().getString(R.string.bookmark_title)
            viewHolder.iconHeader.backgroundDrawable = ContextCompat.getDrawable(appContext(), R.drawable.ic_fav_selected)
            viewHolder.iconHeader.setColorFilter(ContextCompat.getColor(appContext(), android.R.color.black))
        } else
            Picasso.with(viewHolder.iconHeader.context)
                    .load(File(iconPath))
                    .resize(appContext().resources.getDimensionPixelSize(R.dimen.width_icon),
                            appContext().resources.getDimensionPixelSize(R.dimen.height_icon))
                    .into(viewHolder.iconHeader.iconHeader)
    }

    override fun getLayout() = R.layout.lesson_menu_head

}