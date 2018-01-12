package org.secfirst.umbrella.fragments;


import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.FeedAdapter;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaRestClient;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedListFragment extends Fragment {


    private ListView mFeedListView;
    private List<FeedItem> mItems = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Global mGlobal;
    private ExpandableLinearLayout mExpandableLayout;
    private RelativeLayout mButtonLayout;
    private TextView mChangeLocationLabel;
    public static final String CHANGED_LOCATION = "changed_location";
    private TextView mLocationLabel;
    private TextView mColonLabel;
    private TextView mExpandLocationLabel;


    public static FeedListFragment newInstance(List<FeedItem> items) {
        FeedListFragment fragment = new FeedListFragment();
        fragment.mItems = items;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_feed_ist_refresh);
        mFeedListView = (ListView) view.findViewById(R.id.feed_list);
        mExpandableLayout = (ExpandableLinearLayout) view.findViewById(R.id.expandableLayout);
        mButtonLayout = (RelativeLayout) view.findViewById(R.id.button);
        mLocationLabel = (TextView) view.findViewById(R.id.current_location);
        mExpandLocationLabel = (TextView) view.findViewById(R.id.expand_current_location);
        mGlobal = ((BaseActivity) getActivity()).getGlobal();
        mChangeLocationLabel = (TextView) view.findViewById(R.id.expand_change_location);
        mColonLabel = (TextView) view.findViewById(R.id.colon_id);
        initSwipeRefresh();
        initExpandableList();
        initChangeLocation();

        FeedAdapter mFeedAdapter = new FeedAdapter(getActivity(), mItems);
        mFeedListView.setAdapter(mFeedAdapter);
        mFeedListView.setDividerHeight(20);
        mLocationLabel.setText(mGlobal.getRegistry("mLocation").getValue());
        return view;
    }

    private void initChangeLocation() {
        mChangeLocationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putBoolean(CHANGED_LOCATION, true);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.root_frame, TabbedFeedFragment.newInstance(bundle, null));
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void initExpandableList() {
        mExpandableLayout.setInRecyclerView(true);
        mExpandableLayout.setInterpolator(Utils.createInterpolator(Utils.ACCELERATE_DECELERATE_INTERPOLATOR));
        mExpandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                createRotateAnimator(mButtonLayout, 0f, 180f).start();
                animationOpen();
            }

            @Override
            public void onPreClose() {
                createRotateAnimator(mButtonLayout, 180f, 0f).start();
                mLocationLabel.setText(mGlobal.getRegistry("mLocation").getValue());
                animationClose();
            }
        });

        mButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpandableLayout.toggle();
            }
        });


    }

    private void animationOpen() {
        mLocationLabel.setVisibility(View.INVISIBLE);
        mColonLabel.setVisibility(View.INVISIBLE);
        mExpandLocationLabel.setText(mGlobal.getRegistry("mLocation").getValue());
        mExpandLocationLabel.setVisibility(View.VISIBLE);
        mChangeLocationLabel.setVisibility(View.VISIBLE);
        mExpandLocationLabel.setAnimation(fadeInAnimation());
    }

    private void animationClose() {
        mChangeLocationLabel.setVisibility(View.INVISIBLE);
        final Animation changeLocationAnimation = fadeOutAnimation();
        changeLocationAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLocationLabel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        final Animation locationLabelAnimation = fadeInAnimation();
        locationLabelAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mColonLabel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLocationLabel.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLocationLabel.setAnimation(locationLabelAnimation);

    }


    private Animation fadeInAnimation() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);
        fadeIn.setStartOffset(300);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mExpandLocationLabel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return fadeIn;
    }

    private Animation fadeOutAnimation() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(300);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mExpandLocationLabel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return fadeOut;
    }


    private void initSwipeRefresh() {
        mSwipeRefreshLayout.setNestedScrollingEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });
    }

    private void initFooterViewOfList() {
        LinearLayout footer = new LinearLayout(getActivity());
        footer.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 50);
        footer.setLayoutParams(lp);
        mFeedListView.addFooterView(footer);
    }

    private void initHeadViwOfList() {
        TextView header = new TextView(getActivity());
        header.setTextColor(getResources().getColor(R.color.white));
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        mFeedListView.addHeaderView(header);

    }

    public void refreshFeed() {
        getFeeds();
    }

    public boolean getFeeds() {
        Registry selISO2 = mGlobal.getRegistry("iso2");
        if (selISO2 != null) {
            List<Registry> selections;
            try {
                selections = mGlobal.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
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
                    String sources = sb.substring(separator.length());
                    final String mUrl = "feed?country=" + selISO2.getValue() + "&sources=" + sources
                            + "&since=" + mGlobal.getFeedItemsRefreshed();

                    UmbrellaRestClient.get(mUrl, null, "", getContext(), new JsonHttpResponseHandler() {

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
                                        mGlobal.getDaoFeedItem().create(receivedItem);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                mFeedListView.smoothScrollToPosition(0);
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
        } else {
            return false;
        }
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
}
