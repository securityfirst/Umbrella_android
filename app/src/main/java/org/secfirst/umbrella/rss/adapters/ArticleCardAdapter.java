package org.secfirst.umbrella.rss.adapters;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.einmalfel.earl.Item;
import com.squareup.picasso.Picasso;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.WebViewActivity;
import org.secfirst.umbrella.rss.entities.CustomFeed;
import org.secfirst.umbrella.util.ShareContentUtil;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougl on 22/01/2018.
 */
public class ArticleCardAdapter extends RecyclerView.Adapter<ArticleCardAdapter.RSSItem> {


    private Context mContext;
    private List mArticleItems = new ArrayList();

    public ArticleCardAdapter(@NonNull CustomFeed customFeed) {
        removeUnformattedItem(customFeed.getFeed().getItems());
    }

    @Override
    public ArticleCardAdapter.RSSItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card_view_item, parent, false);
        mContext = view.getContext();
        return new RSSItem(view);
    }

    @Override
    public void onBindViewHolder(final ArticleCardAdapter.RSSItem holder, final int position) {
        Item item = (Item) mArticleItems.get(position);
        String reportDate = item.getPublicationDate() != null ? UmbrellaUtil.convertDateToString(item.getPublicationDate()) : "";
        String description = item.getDescription() != null ? Html.fromHtml(item.getDescription()).toString() : item.getDescription();
        holder.articleTitle.setText(item.getTitle());
        holder.articleDescription.setText(description);
        holder.articleAuthor.setText(item.getAuthor());
        holder.articleLastUpdate.setText(reportDate);
        Picasso.with(mContext)
                .load(item.getImageLink())
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.default_image))
                .resize(344, 176)
                .into(holder.articleImage);
    }

    @Override
    public int getItemCount() {
        return mArticleItems.size();
    }


    class RSSItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView articleTitle;
        public TextView articleDescription;
        public ImageView articleImage;
        public TextView articleShare;
        public TextView articleLeanMore;
        public TextView articleAuthor;
        public TextView articleLastUpdate;

        RSSItem(View view) {
            super(view);

            articleTitle = view.findViewById(R.id.article_item_title);
            articleDescription = view.findViewById(R.id.article_item_description);
            articleShare = view.findViewById(R.id.article_item_share_text);
            articleLeanMore = view.findViewById(R.id.article_item_lean_more_text);
            articleImage = view.findViewById(R.id.article_item_image);
            articleAuthor = view.findViewById(R.id.article_item_author);
            articleLastUpdate = view.findViewById(R.id.article_item_last_update);

            articleShare.setOnClickListener(this);
            articleLeanMore.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Item currentItem = (Item) mArticleItems.get(getLayoutPosition());

            switch (view.getId()) {
                case R.id.article_item_share_text:

                    ShareContentUtil.shareLinkContent(mContext, currentItem.getLink());
                    break;
                case R.id.article_item_lean_more_text:

                    final String link = currentItem.getLink();
                    if (link != null && Patterns.WEB_URL.matcher(link).matches()) {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.URL_KEY, link);
                        mContext.startActivity(intent);
                    }
                    break;
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


