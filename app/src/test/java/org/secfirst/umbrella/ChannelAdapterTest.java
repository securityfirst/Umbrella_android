package org.secfirst.umbrella;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.secfirst.umbrella.rss.feed.ChannelAdapter;
import org.secfirst.umbrella.rss.feed.CustomFeed;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by dougl on 20/01/2018.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ChannelAdapterTest {

    private Context context;
    private ChannelAdapter.RSSChannel viewHolder;
    private ChannelAdapter channelAdapter;

    @Before
    public void setUp() throws Exception {
        CustomFeed customFeed = new CustomFeed();
        customFeed.setTitle("title1");

        CustomFeed customFeed1 = new CustomFeed();
        customFeed1.setTitle("title2");

        CustomFeed customFeed2 = new CustomFeed();
        customFeed1.setTitle("title3");

        context = RuntimeEnvironment.application;
        List<CustomFeed> customFeeds = new ArrayList<>();
        customFeeds.add(customFeed);
        customFeeds.add(customFeed1);
        customFeeds.add(customFeed2);
        channelAdapter = new ChannelAdapter(customFeeds);
        RecyclerView rvParent = new RecyclerView(context);
        rvParent.setLayoutManager(new LinearLayoutManager(context));
        viewHolder = channelAdapter.onCreateViewHolder(rvParent, 0);
    }

    @Test
    public void getOneItemInList() {
        channelAdapter.onBindViewHolder(viewHolder, 0);
        assertEquals("title1", viewHolder.channelTitle.getText().toString());
    }

    @Test
    public void checkSizeList() {
        assertEquals(3, channelAdapter.getItemCount());
    }

    @Test
    public void AddOneItemInList() {
        CustomFeed customFeed = new CustomFeed();
        customFeed.setTitle("title");
        channelAdapter.add(customFeed);
        channelAdapter.onBindViewHolder(viewHolder, 3);
        assertEquals(customFeed.getTitle(), viewHolder.channelTitle.getText().toString());
    }

    @Test
    public void removeOneItemInList() {
        CustomFeed customFeed = new CustomFeed();
        channelAdapter.add(customFeed);
        channelAdapter.remove(customFeed);
        assertEquals(channelAdapter.getItemCount(), 3);
    }

    @Test
    public void sendToAdapterAnEmptyList() {
        ChannelAdapter channelAdapter = new ChannelAdapter(new ArrayList<CustomFeed>());
        RecyclerView rvParent = new RecyclerView(context);
        rvParent.setLayoutManager(new LinearLayoutManager(context));
        assertEquals(channelAdapter.getItemCount(), 0);
    }

    @Test
    public void clearMyListInAdapter() {
        channelAdapter.clear();
        assertEquals(channelAdapter.getItemCount(), 0);
    }
}
