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

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.rss.api.Article;
import org.secfirst.umbrella.rss.api.Channel;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChannelFragment extends Fragment implements View.OnClickListener, ChannelContract.View {


    private FloatingActionButton mAddFeedButton;
    private RecyclerView mChannelRecyclerView;
    private ChannelAdapter mChannelAdapter;
    private List<Channel> mChannels;
    private ChannelContract.Presenter mPresenter;

    public ChannelFragment() {
        // Required empty public constructor
    }

    public static ChannelFragment newInstance() {
        Bundle args = new Bundle();
        ChannelFragment fragment = new ChannelFragment();
        fragment.setArguments(args);

        fragment.mChannels = new ArrayList<>();
        Channel channel = new Channel("Ttitle 1", "Description1", new ArrayList<Article>());
        Channel channel1 = new Channel("Ttitle 2", "Description2", new ArrayList<Article>());

        fragment.mChannels.add(channel);
        fragment.mChannels.add(channel1);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_rss, container, false);
        mAddFeedButton = view.findViewById(R.id.add_feed_btn);
        mAddFeedButton.setOnClickListener(this);
        mChannelRecyclerView = view.findViewById(R.id.channel_recycler_view);
        mChannelAdapter = new ChannelAdapter(mChannels);
        mChannelRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mChannelRecyclerView.setLayoutManager(mLayoutManager);
        mChannelRecyclerView.setAdapter(mChannelAdapter);
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
                ChannelDialog channelDialog = ChannelDialog.newInstance();
                channelDialog.show(fragmentManager, "");
                break;
        }
    }

    @Override
    public void setPresenter(@NonNull ChannelContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void loadInProgressFeed() {

    }

    @Override
    public void finishLoadFeed() {

    }
}
