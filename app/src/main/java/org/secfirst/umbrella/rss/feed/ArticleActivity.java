package org.secfirst.umbrella.rss.feed;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;

public class ArticleActivity extends BaseActivity {

    // TODO: 02/02/2018  That's impossible to parcelable [com.einmalfel.earl.Feed]
    // TODO: Probably we have to create a PM in Earl lib.
    public static CustomFeed mCustomFeed;
    private RecyclerView mArticleRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableActionBar();
        mArticleRecyclerView = findViewById(R.id.article_recycler_view);
        openCardListAdapter();
    }

    private void enableActionBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_feed, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.action_card_list:
                setUplMenuItem(item);
                break;
        }
        return true;
    }

    private void setUplMenuItem(MenuItem menuItem) {
        if (menuItem.getTitle().equals(getString(R.string.simple_list))) {
            menuItem.setTitle(getString(R.string.card_list));
            openSimpleListAdapter(menuItem);
        } else {
            menuItem.setTitle(getString(R.string.simple_list));
            openCardListAdapter();
        }
    }

    private void openCardListAdapter() {
        ArticleCardAdapter articleCardAdapter = new ArticleCardAdapter(mCustomFeed);
        mArticleRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mArticleRecyclerView.setLayoutManager(mLayoutManager);
        mArticleRecyclerView.setAdapter(articleCardAdapter);
    }

    @SuppressWarnings("ConstantConditions")
    private void openSimpleListAdapter(MenuItem menuItem) {
        ArticleSimpleAdapter simpleAdapter = new ArticleSimpleAdapter(mCustomFeed);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        itemDecor.setDrawable(ContextCompat.getDrawable(this, R.drawable.article_list_divider));
        mArticleRecyclerView.addItemDecoration(itemDecor);
        mArticleRecyclerView.setAdapter(simpleAdapter);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_article;
    }

}
