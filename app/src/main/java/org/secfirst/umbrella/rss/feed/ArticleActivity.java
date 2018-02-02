package org.secfirst.umbrella.rss.feed;

import android.os.Bundle;
import android.view.MenuItem;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;

public class ArticleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
