package org.secfirst.umbrella.rss.feed;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.einmalfel.earl.Item;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.rss.feed.adapters.ArticleCardAdapter;
import org.secfirst.umbrella.rss.feed.adapters.ArticleSimpleAdapter;
import org.secfirst.umbrella.util.RecyclerItemClickListener;
import org.secfirst.umbrella.util.ShareContentUtil;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.List;

public class ArticleActivity extends BaseActivity {

    // TODO: 02/02/2018  That's no way to parcelable [com.einmalfel.earl.Feed]
    // TODO: Probably we have to create a PM in Earl lib.
    public static CustomFeed mCustomFeed;
    private RecyclerView mArticleRecyclerView;
    private LinearLayoutManager mLayoutManager;
    //private MenuItem mShareIcon;
    private ActionMode mActionMode;
    private ArticleSimpleAdapter simpleAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableActionBar();
        mArticleRecyclerView = findViewById(R.id.article_recycler_view);
        openCardListAdapter();
    }

    private void enableActionBar() {
        toolbar = findViewById(R.id.article_list_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mCustomFeed.getTitle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_feed_list, menu);
        //mShareIcon = menu.findItem(R.id.action_share);
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
            //mShareIcon.setVisible(true);
        } else {
            menuItem.setTitle(getString(R.string.simple_list));
            openCardListAdapter();
            //mShareIcon.setVisible(false);
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
        simpleAdapter = new ArticleSimpleAdapter(mCustomFeed);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        itemDecor.setDrawable(ContextCompat.getDrawable(this, R.drawable.article_list_divider));
        mArticleRecyclerView.addItemDecoration(itemDecor);
        mArticleRecyclerView.setAdapter(simpleAdapter);
        mArticleRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                mArticleRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (simpleAdapter.isMultiSelect()) {
                    multiSelect(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!simpleAdapter.isMultiSelect()) {
                    simpleAdapter.setMultiSelect(true);

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback); //show ActionMode.
                    }
                }

                multiSelect(position);
            }
        }));
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.custom_feed_list1, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.article_action_share:
                    String stringToShare = UmbrellaUtil.splitArticleLinkToShare(simpleAdapter.getSelectedItems());
                    ShareContentUtil.shareLinkContent(getBaseContext(), stringToShare);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            simpleAdapter.cleanSelectedItems();
            mActionMode = null;
            simpleAdapter.setMultiSelect(false);
        }
    };

    private void multiSelect(int position) {
        Item itemSelected = (Item) simpleAdapter.getArticleItems().get(position);
        List<Item> itemsSelected = simpleAdapter.getSelectedItems();
        if (itemSelected != null) {
            if (mActionMode != null) {
                if (itemsSelected.contains(itemSelected)) {
                    itemsSelected.remove(itemSelected);
                } else {
                    simpleAdapter.addSelectedItems(itemSelected);
                }

                if (itemsSelected.size() > 0)
                    mActionMode.setTitle(String.valueOf(itemsSelected.size())); //show selected item count on action mode.
                else {
                    mActionMode.setTitle(""); //remove item count from action mode.
                    mActionMode.finish(); //hide action mode.
                }
                simpleAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_article;
    }
}
