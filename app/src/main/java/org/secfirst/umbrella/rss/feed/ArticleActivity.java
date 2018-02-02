package org.secfirst.umbrella.rss.feed;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;

public class ArticleActivity extends BaseActivity {

    // TODO: 02/02/2018  That's impossible to parcelable [com.einmalfel.earl.Feed]
    public static CustomFeed mCustomFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableActionBar();
        RecyclerView articleRecyclerView = findViewById(R.id.article_recycler_view);
        ArticleAdapter articleAdapter = new ArticleAdapter(mCustomFeed);
        articleRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        articleRecyclerView.setLayoutManager(mLayoutManager);
        articleRecyclerView.setAdapter(articleAdapter);
    }

    private void enableActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_article;
    }
}
