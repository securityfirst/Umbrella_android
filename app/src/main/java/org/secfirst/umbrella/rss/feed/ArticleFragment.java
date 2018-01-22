package org.secfirst.umbrella.rss.feed;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.einmalfel.earl.Feed;

import org.secfirst.umbrella.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleFragment extends Fragment {


    private Feed mFeed;
    private RecyclerView mArticleRecyclerView;
    private ArticleAdapter mArticleAdapter;

    public ArticleFragment() {
        // Required empty public constructor
    }

    public static ArticleFragment newInstance(Feed feed) {
        Bundle args = new Bundle();
        ArticleFragment fragment = new ArticleFragment();
        fragment.mFeed = feed;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        mArticleRecyclerView = view.findViewById(R.id.article_recycler_view);
        mArticleAdapter = new ArticleAdapter(mFeed);
        mArticleRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mArticleRecyclerView.setLayoutManager(mLayoutManager);
        mArticleRecyclerView.setAdapter(mArticleAdapter);
        return view;
    }

}
