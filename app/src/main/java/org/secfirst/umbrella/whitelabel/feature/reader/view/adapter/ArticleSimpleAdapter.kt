package org.secfirst.umbrella.whitelabel.feature.reader.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.simple_article_view.view.*
import org.jsoup.Jsoup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.reader.Article
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.misc.convertDateToString

class ArticleSimpleAdapter(private val onClickLearnMore: (Article) -> Unit) : RecyclerView.Adapter<ArticleSimpleAdapter.SimpleHolder>() {

    private var items: MutableList<Article> = mutableListOf()


    fun addAll(rss: RSS) {
        rss.items_.forEach { item ->
            items.add(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_article_view, parent, false)
        return SimpleHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SimpleHolder, position: Int) {
        holder.bind(items[position], clickListener = { onClickLearnMore(items[position]) })
    }

    class SimpleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(articleItem: Article, clickListener: (SimpleHolder) -> Unit) {
            with(articleItem) {
                itemView.title.text = title
                itemView.description.text = Jsoup.parse(description_).text()
                itemView.lastUpdate.text = convertDateToString(publicationDate)
                itemView.setOnClickListener { clickListener(this@SimpleHolder) }
            }
        }
    }
}