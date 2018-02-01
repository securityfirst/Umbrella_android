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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.RSS;
import org.secfirst.umbrella.util.RecyclerItemTouchHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment implements View.OnClickListener, FeedContract.View,
        ChannelDialog.OnChannelDialog, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {


    private ChannelAdapter mChannelAdapter;
    private FeedContract.Presenter mPresenter;
    private ProgressBar mRssProgress;
    private CoordinatorLayout mCoordinatorLayout;

    public ChannelFragment() {
        // Required empty public constructor
    }

    public static ChannelFragment newInstance() {
        Bundle args = new Bundle();
        ChannelFragment fragment = new ChannelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_rss, container, false);
        mPresenter = new FeedPresenter(this);
        FloatingActionButton mAddFeedButton = view.findViewById(R.id.add_feed_btn);
        mAddFeedButton.setOnClickListener(this);
        mChannelAdapter = new ChannelAdapter(new ArrayList<CustomFeed>());
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_feed_btn:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ChannelDialog channelDialog = ChannelDialog.newInstance(this);
                channelDialog.show(fragmentManager, "");
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mChannelAdapter.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mChannelAdapter.clear();
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
        mChannelAdapter.add(customFeeds);
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
            mChannelAdapter.add(customFeed);
        }
    }

    @Override
    public void errorDeleteFeed() {
        Toast.makeText(getContext(), R.string.error_feed_channel_fragment, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ChannelAdapter.RSSChannel) {
            String name = mChannelAdapter.getCustomFeeds().get(viewHolder.getAdapterPosition()).getTitle();
            final CustomFeed customFeed = mChannelAdapter.getCustomFeeds().get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            mChannelAdapter.remove(mChannelAdapter.getCustomFeeds().get(viewHolder.getAdapterPosition()));
            Snackbar snackbar = Snackbar
                    .make(mCoordinatorLayout, name + " " + getString(R.string.rss_custom_remove_item), Snackbar.LENGTH_LONG);
            mPresenter.removeFeed(customFeed);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mChannelAdapter.restoreItem(customFeed, deletedIndex);
                    mPresenter.saveFeed(customFeed);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void setUpRecycleView(View view) {
        RecyclerView channelRecyclerView = view.findViewById(R.id.channel_recycler_view);
        channelRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        channelRecyclerView.setLayoutManager(mLayoutManager);
        channelRecyclerView.setAdapter(mChannelAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(channelRecyclerView);
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
}
