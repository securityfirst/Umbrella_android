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
import org.secfirst.umbrella.rss.api.Article;
import org.secfirst.umbrella.rss.api.Channel;
import org.secfirst.umbrella.rss.feed.ChannelAdapter;

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

        List<Article> articles = new ArrayList<>();
        List<Channel> channels = new ArrayList<>();
        Channel channel1 = new Channel("title1", "description1", articles);
        Channel channel2 = new Channel("title2", "description2", articles);
        Channel channel3 = new Channel("title3", "description3", articles);

        channels.add(channel1);
        channels.add(channel2);
        channels.add(channel3);
        context = RuntimeEnvironment.application;


        channelAdapter = new ChannelAdapter(channels);
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
        Channel channel4 = new Channel("title4", "description4", new ArrayList<Article>());
        channelAdapter.add(channel4);
        channelAdapter.onBindViewHolder(viewHolder, 3);
        assertEquals(channel4.getTitle(), viewHolder.channelTitle.getText().toString());
    }

    @Test
    public void removeOneItemInList() {
        Channel channel4 = new Channel("title4", "description4", new ArrayList<Article>());
        channelAdapter.add(channel4);
        channelAdapter.remove(channel4);
        assertEquals(channelAdapter.getItemCount(), 3);
    }

    @Test
    public void sendToAdapterAnEmptyList() {
        ChannelAdapter channelAdapter = new ChannelAdapter(new ArrayList<Channel>());
        RecyclerView rvParent = new RecyclerView(context);
        rvParent.setLayoutManager(new LinearLayoutManager(context));
        assertEquals(channelAdapter.getItemCount(), 0);
    }

}
