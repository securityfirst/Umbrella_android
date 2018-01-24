package org.secfirst.umbrella.rss.feed;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.einmalfel.earl.Feed;

import org.secfirst.umbrella.R;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment implements View.OnClickListener, FeedContract.View, ChannelDialog.OnChannelDialog {


    private FloatingActionButton mAddFeedButton;
    private ChannelAdapter mChannelAdapter;
    private FeedContract.Presenter mPresenter;
    private ProgressBar mRssProgress;

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
        mAddFeedButton = view.findViewById(R.id.add_feed_btn);
        mAddFeedButton.setOnClickListener(this);
        RecyclerView mChannelRecyclerView = view.findViewById(R.id.channel_recycler_view);
        mChannelAdapter = new ChannelAdapter(new ArrayList<Feed>());
        mChannelRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mChannelRecyclerView.setLayoutManager(mLayoutManager);
        mChannelRecyclerView.setAdapter(mChannelAdapter);
        mRssProgress = view.findViewById(R.id.rss_indeterminate_bar);
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
    public void setPresenter(@NonNull FeedContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter, "FeedPresenter cannot be null!");
    }

    @Override
    public void setLoadingIndicator() {
        mRssProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorLoadFeed() {
        mRssProgress.setVisibility(View.INVISIBLE);
        Toast.makeText(getContext(), "There is a problem to load your RSS.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishLoadFeed(Feed feed) {
        mRssProgress.setVisibility(View.INVISIBLE);
        mChannelAdapter.add(feed);
    }

    @Override
    public void getFeedFromDialog(String url) {
        mPresenter.loadFeed(url);
    }
}