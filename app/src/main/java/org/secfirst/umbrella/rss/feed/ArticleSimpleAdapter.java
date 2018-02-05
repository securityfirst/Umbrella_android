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
import android.widget.TextView;

import com.einmalfel.earl.Item;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.UmbrellaUtil;
import org.secfirst.umbrella.util.WebViewDialog;

import java.util.List;

/**
 * Created by dougl on 22/01/2018.
 */
public class ArticleSimpleAdapter extends RecyclerView.Adapter<ArticleSimpleAdapter.RSSItem> {


    private final List<? extends Item> mArticleItems;
    private Context mContext;

    public ArticleSimpleAdapter(@NonNull CustomFeed customFeed) {
        this.mArticleItems = customFeed.getFeed().getItems();
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
        Item item = mArticleItems.get(position);
        String reportDate = UmbrellaUtil.convertDateToString(item.getPublicationDate());
        String description = Html.fromHtml(item.getDescription()).toString();

        holder.articleTitle.setText(item.getTitle());
        holder.articleDescription.setText(description);
        holder.articleLastUpdate.setText(reportDate);
    }

    @Override
    public int getItemCount() {
        return mArticleItems.size();
    }


    class RSSItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView articleTitle;
        public TextView articleDescription;
        public TextView articleLastUpdate;

        RSSItem(View view) {
            super(view);

            articleTitle = view.findViewById(R.id.article_simple_list_title);
            articleDescription = view.findViewById(R.id.article_simple_list_description);
            articleLastUpdate = view.findViewById(R.id.article_simple_list_last_update);

            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            final String link = mArticleItems.get(getLayoutPosition()).getLink();
            if (link != null && Patterns.WEB_URL.matcher(link).matches()) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                WebViewDialog webViewDialog = WebViewDialog.newInstance(link);
                webViewDialog.show(fragmentManager, "");
            }
        }
    }
}


