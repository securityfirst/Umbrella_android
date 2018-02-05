package org.secfirst.umbrella.rss.feed;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.RSS;
import org.secfirst.umbrella.util.RecyclerItemClickListener;
import org.secfirst.umbrella.util.RecyclerItemTouchHelper;
import org.secfirst.umbrella.util.ShareContentUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class CustomFeedFragment extends Fragment implements View.OnClickListener, FeedContract.View,
        CustomFeedDialog.OnChannelDialog, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private ActionMode mActionMode;
    private CustomFeedAdapter mFeedAdapter;
    private FeedContract.Presenter mPresenter;
    private ProgressBar mRssProgress;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mAddFeedButton;


    public CustomFeedFragment() {
        // Required empty public constructor
    }

    public static CustomFeedFragment newInstance() {
        Bundle args = new Bundle();
        CustomFeedFragment fragment = new CustomFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_rss, container, false);
        mPresenter = new FeedPresenter(this);
        mAddFeedButton = view.findViewById(R.id.add_feed_btn);
        mAddFeedButton.setOnClickListener(this);
        mFeedAdapter = new CustomFeedAdapter(new ArrayList<CustomFeed>(), getActivity());
        setUpRecycleView(view);
        mRssProgress = view.findViewById(R.id.rss_indeterminate_bar);
        mCoordinatorLayout = view.findViewById(R.id.rss_coordinator_layout);
        loadDefaultFeeds();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        mAddFeedButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_feed_btn:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                CustomFeedDialog customFeedDialog = CustomFeedDialog.newInstance(this);
                customFeedDialog.show(fragmentManager, "");
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mFeedAdapter.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFeedAdapter.clear();
    }

    @Override
    public void setPresenter(@NonNull FeedContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter, "FeedPresenter cannot be null!");
    }

    @Override
    public void setLoadingIndicator() {
        mRssProgress.setVisibility(View.VISIBLE);
    }


    @Override
    public void finishLoadFeed(List<CustomFeed> customFeeds) {
        mRssProgress.setVisibility(View.INVISIBLE);
        mFeedAdapter.add(customFeeds);
        mPresenter.saveFeed(customFeeds);
    }

    @Override
    public void getFeedFromDialog(String url) {
        mPresenter.loadFeed(url);
    }

    @Override
    public void errorLoadFeed() {
        mRssProgress.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(), R.string.rss_error_load_feed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void errorSaveFeed() {
        Toast.makeText(getContext(), R.string.error_channel_fragment, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFeeds(List<CustomFeed> customFeeds) {
        for (CustomFeed customFeed : customFeeds) {
            mFeedAdapter.add(customFeed);
        }
    }

    @Override
    public void errorDeleteFeed() {
        Toast.makeText(getContext(), R.string.error_feed_channel_fragment, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CustomFeedAdapter.RSSChannel) {
            String name = mFeedAdapter.getCustomFeeds().get(viewHolder.getAdapterPosition()).getTitle();
            final CustomFeed customFeed = mFeedAdapter.getCustomFeeds().get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            mFeedAdapter.remove(mFeedAdapter.getCustomFeeds().get(viewHolder.getAdapterPosition()));
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, name + " " + getString(R.string.rss_custom_remove_item), Snackbar.LENGTH_LONG);
            mPresenter.removeFeed(customFeed);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mFeedAdapter.restoreItem(customFeed, deletedIndex);
                    mPresenter.saveFeed(customFeed);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void setUpRecycleView(View view) {
        RecyclerView feedRecyclerView = view.findViewById(R.id.channel_recycler_view);
        feedRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        feedRecyclerView.setLayoutManager(mLayoutManager);
        feedRecyclerView.setAdapter(mFeedAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(feedRecyclerView);

        feedRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                feedRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFeedAdapter.isMultiSelect()) {
                    multiSelect(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!mFeedAdapter.isMultiSelect()) {
                    mFeedAdapter.setMultiSelect(true);

                    if (mActionMode == null) {
                        mActionMode = getActivity().startActionMode(mActionModeCallback); //show ActionMode.
                    }
                }

                multiSelect(position);
            }
        }));
    }

    private void multiSelect(int position) {
        CustomFeed customFeed = mFeedAdapter.getCustomFeeds().get(position);
        List<CustomFeed> selectedFeeds = mFeedAdapter.getSelectedFeeds();
        if (customFeed != null) {
            if (mActionMode != null) {
                if (selectedFeeds.contains(customFeed)) {
                    selectedFeeds.remove(customFeed);
                } else {
                    mFeedAdapter.addSelectedFeeds(customFeed);
                }

                if (selectedFeeds.size() > 0)
                    mActionMode.setTitle(String.valueOf(selectedFeeds.size())); //show selected item count on action mode.
                else {
                    mActionMode.setTitle(""); //remove item count from action mode.
                    mActionMode.finish(); //hide action mode.
                }
                mFeedAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadDefaultFeeds() {
        if (!RSS.isLoadedDefault(getActivity())) {
            InputStream inputStream;
            try {
                inputStream = getActivity().getAssets().open(RSS.FILE_NAME);
                String[] urls = mPresenter.getDefaultFeedUrl(inputStream);
                mPresenter.loadFeed(urls);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            getActivity().getMenuInflater().inflate(R.menu.custom_feed, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_rss_delete:
                    for (CustomFeed feed : mFeedAdapter.getSelectedFeeds()) {
                        mFeedAdapter.remove(feed);
                        mPresenter.removeFeed(feed);
                    }
                    return true;
                case R.id.action_rss_share:
                    String stringToShare = mPresenter.splitFeedLinkToShare(mFeedAdapter.getSelectedFeeds());
                    ShareContentUtil.shareLinkContent(getContext(), stringToShare);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mFeedAdapter.cleanSelectedFeeds();
            mActionMode = null;
            mFeedAdapter.setMultiSelect(false);
        }
    };

}
