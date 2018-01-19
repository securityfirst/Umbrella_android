package org.secfirst.umbrella.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaRestClient;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class FeedEmptyFragment extends Fragment {

    private View mView;
    private CardView mEmptyCard;
    private String mLocation;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static FeedEmptyFragment newInstance(String location) {
        Bundle args = new Bundle();
        FeedEmptyFragment fragment = new FeedEmptyFragment();
        fragment.setArguments(args);
        fragment.mLocation = location;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dasboard_feed_empty_view, container, false);
        mEmptyCard = (CardView) mView.findViewById(R.id.empty_dashboard);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.fragment_feed_empty_refresh);
        TextView title = (TextView) mView.findViewById(R.id.empty_dashboard_title);
        title.setText(mLocation);
        mEmptyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                TabbedFeedFragment tabbedFeedFragment = new TabbedFeedFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(FeedListFragment.CHANGED_LOCATION, true);
                tabbedFeedFragment.setArguments(bundle);
                transaction.replace(R.id.root_frame, tabbedFeedFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        initSwipeRefresh();
        return mView;
    }

    private void initSwipeRefresh() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFeeds(getContext());
            }
        });
    }

    private boolean getFeeds(final Context context) {
        Registry selISO2 = Global.INSTANCE.getRegistry("iso2");
        if (selISO2 != null) {
            List<Registry> selections;
            try {
                selections = Global.INSTANCE.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                if (selections.size() > 0) {
                    String separator = ",";
                    int total = selections.size() * separator.length();
                    for (Registry item : selections) {
                        total += item.getValue().length();
                    }
                    StringBuilder sb = new StringBuilder(total);
                    for (Registry item : selections) {
                        sb.append(separator).append(item.getValue());
                    }

                    //TODO remove since "since=0" before commit this code.
                    // *mGlobal.getFeedItemsRefreshed()

                    String sources = sb.substring(separator.length());
                    final String mUrl = "feed?country=" + selISO2.getValue() + "&sources=" + sources
                            + "&since=" + 0;

                    UmbrellaRestClient.get(mUrl, null, "", context, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<ArrayList<FeedItem>>() {
                            }.getType();
                            ArrayList<FeedItem> receivedItems = gson.fromJson(response.toString(), listType);
                            if (receivedItems != null && receivedItems.size() > 0) {
                                for (FeedItem receivedItem : receivedItems) {
                                    try {
                                        Global.INSTANCE.getDaoFeedItem().create(receivedItem);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                        .beginTransaction();
                                transaction.replace(R.id.root_frame, FeedListFragment.
                                        newInstance(receivedItems));
                                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } else {
                                Toast.makeText(getActivity(), R.string.error_refresh_message, Toast.LENGTH_SHORT).show();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (throwable instanceof javax.net.ssl.SSLPeerUnverifiedException) {
                                Toast.makeText(getContext(), "The SSL certificate pin is not valid." +
                                        " Most likely the certificate has expired and was renewed. Update " +
                                        "the app to refresh the accepted pins", Toast.LENGTH_LONG).show();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    });
                } else {
                    Toast.makeText(getActivity(), R.string.no_sources_selected, Toast.LENGTH_SHORT).show();
                }
                return true;
            } catch (SQLException e) {
                Timber.e(e);
            }
            return false;
        }
        return false;
    }

}
