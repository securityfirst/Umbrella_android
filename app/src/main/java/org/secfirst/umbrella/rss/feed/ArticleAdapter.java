package org.secfirst.umbrella.rss.feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;
import com.squareup.picasso.Picasso;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.ShareContentUtil;
import org.secfirst.umbrella.util.UmbrellaUtil;
import org.secfirst.umbrella.util.WebViewDialog;

import java.util.List;

/**
 * Created by dougl on 22/01/2018.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.RSSItem> {


    private final List<? extends Item> mArticleItems;
    private Context mContext;

    public ArticleAdapter(@NonNull Feed feed) {
        this.mArticleItems = feed.getItems();
    }

    @Override
    public ArticleAdapter.RSSItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_item, parent, false);
        mContext = view.getContext();
        return new RSSItem(view);
    }

    @Override
    public void onBindViewHolder(final ArticleAdapter.RSSItem holder, final int position) {
        Item item = mArticleItems.get(position);
        String reportDate = UmbrellaUtil.convertDateToString(item.getPublicationDate());
        String description = Html.fromHtml(item.getDescription()).toString();

        holder.articleTitle.setText(item.getTitle());
        holder.articleDescription.setText(description);
        holder.articleAuthor.setText(item.getAuthor());
        holder.articleLastUpdate.setText(reportDate);
        Picasso.with(mContext)
                .load(item.getImageLink())
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
            switch (view.getId()) {
                case R.id.article_item_share_text:

                    ShareContentUtil.shareLinkContent(mContext, mArticleItems.get(getLayoutPosition()).getLink());
                    break;
                case R.id.article_item_lean_more_text:

                    final String link = mArticleItems.get(getLayoutPosition()).getLink();
                    if (link != null && Patterns.WEB_URL.matcher(link).matches()) {
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        WebViewDialog webViewDialog = WebViewDialog.newInstance(link);
                        webViewDialog.show(fragmentManager, "");
                    }
                    break;
            }
        }
    }
}


