package org.secfirst.umbrella.rss.feed.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.einmalfel.earl.Item;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.WebViewActivity;
import org.secfirst.umbrella.rss.feed.CustomFeed;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougl on 22/01/2018.
 */
public class ArticleSimpleAdapter extends RecyclerView.Adapter<ArticleSimpleAdapter.RSSItem> {


    private List mArticleItems = new ArrayList();
    private List<Item> mSelectedItems = new ArrayList<>();
    private Context mContext;
    private boolean isMultiSelect = false;

    public ArticleSimpleAdapter(@NonNull CustomFeed customFeed) {
        removeUnformattedItem(customFeed.getFeed().getItems());
    }

    public void cleanSelectedItems() {
        mSelectedItems = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addSelectedItems(Item customFeed) {
        mSelectedItems.add(customFeed);
        notifyDataSetChanged();
    }

    public List<Item> getSelectedItems() {
        return mSelectedItems;
    }


    public List getArticleItems() {
        return mArticleItems;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    @Override
    public ArticleSimpleAdapter.RSSItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_simple_view_item, parent, false);
        mContext = view.getContext();
        return new RSSItem(view);
    }

    @Override
    public void onBindViewHolder(final ArticleSimpleAdapter.RSSItem holder, final int position) {
        Item item = (Item) mArticleItems.get(position);
        String reportDate = UmbrellaUtil.convertDateToString(item.getPublicationDate());
        String description = Html.fromHtml(item.getDescription()).toString();

        holder.articleTitle.setText(item.getTitle());
        holder.articleDescription.setText(description);
        holder.articleLastUpdate.setText(reportDate);

        if (mSelectedItems.contains(item)) {
            //if item is selected then,set foreground color of FrameLayout.
            holder.articleForeground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
        } else {
            //else remove selected item color.
            holder.articleForeground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mArticleItems.size();
    }


    class RSSItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView articleTitle;
        public TextView articleDescription;
        public TextView articleLastUpdate;
        public LinearLayout articleForeground;

        RSSItem(View view) {
            super(view);

            articleTitle = view.findViewById(R.id.article_simple_list_title);
            articleDescription = view.findViewById(R.id.article_simple_list_description);
            articleLastUpdate = view.findViewById(R.id.article_simple_list_last_update);
            articleForeground = view.findViewById(R.id.article_item);

            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Item currentItem = (Item) mArticleItems.get(getLayoutPosition());
            final String link = currentItem.getLink();
            if (link != null && Patterns.WEB_URL.matcher(link).matches()) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URL_KEY, link);
                mContext.startActivity(intent);
            }
        }
    }


    @SuppressWarnings("unchecked")
    private void removeUnformattedItem(List<? extends Item> items) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getDescription() != null && !item.getDescription().equals("")) {
                mArticleItems.add(items.get(i));
            }
        }
    }
}


