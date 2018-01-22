package org.secfirst.umbrella;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.secfirst.umbrella.rss.feed.ChannelAdapter;

import java.util.ArrayList;
import java.util.Date;
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
        Feed feed1 = new Feed() {
            @Nullable
            @Override
            public String getLink() {
                return null;
            }

            @Nullable
            @Override
            public Date getPublicationDate() {
                return null;
            }

            @NonNull
            @Override
            public String getTitle() {
                return "title1";
            }

            @Nullable
            @Override
            public String getDescription() {
                return "description1";
            }

            @Nullable
            @Override
            public String getCopyright() {
                return null;
            }

            @Nullable
            @Override
            public String getImageLink() {
                return null;
            }

            @Nullable
            @Override
            public String getAuthor() {
                return null;
            }

            @NonNull
            @Override
            public List<? extends Item> getItems() {
                return null;
            }
        };

        Feed feed2 = new Feed() {
            @Nullable
            @Override
            public String getLink() {
                return null;
            }

            @Nullable
            @Override
            public Date getPublicationDate() {
                return null;
            }

            @NonNull
            @Override
            public String getTitle() {
                return "title2";
            }

            @Nullable
            @Override
            public String getDescription() {
                return "description2";
            }

            @Nullable
            @Override
            public String getCopyright() {
                return null;
            }

            @Nullable
            @Override
            public String getImageLink() {
                return null;
            }

            @Nullable
            @Override
            public String getAuthor() {
                return null;
            }

            @NonNull
            @Override
            public List<? extends Item> getItems() {
                return null;
            }
        };

        Feed feed3 = new Feed() {
            @Nullable
            @Override
            public String getLink() {
                return null;
            }

            @Nullable
            @Override
            public Date getPublicationDate() {
                return null;
            }

            @NonNull
            @Override
            public String getTitle() {
                return "title3";
            }

            @Nullable
            @Override
            public String getDescription() {
                return "description3";
            }

            @Nullable
            @Override
            public String getCopyright() {
                return null;
            }

            @Nullable
            @Override
            public String getImageLink() {
                return null;
            }

            @Nullable
            @Override
            public String getAuthor() {
                return null;
            }

            @NonNull
            @Override
            public List<? extends Item> getItems() {
                return null;
            }
        };
        List<Feed> feedList = new ArrayList<>();
        feedList.add(feed1);
        feedList.add(feed2);
        feedList.add(feed3);
        context = RuntimeEnvironment.application;
        channelAdapter = new ChannelAdapter(feedList);
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
        Feed feed = new Feed() {
            @Nullable
            @Override
            public String getLink() {
                return null;
            }

            @Nullable
            @Override
            public Date getPublicationDate() {
                return null;
            }

            @NonNull
            @Override
            public String getTitle() {
                return "new item";
            }

            @Nullable
            @Override
            public String getDescription() {
                return "new descritpion";
            }

            @Nullable
            @Override
            public String getCopyright() {
                return null;
            }

            @Nullable
            @Override
            public String getImageLink() {
                return null;
            }

            @Nullable
            @Override
            public String getAuthor() {
                return null;
            }

            @NonNull
            @Override
            public List<? extends Item> getItems() {
                return null;
            }
        };
        channelAdapter.add(feed);
        channelAdapter.onBindViewHolder(viewHolder, 3);
        assertEquals(feed.getTitle(), viewHolder.channelTitle.getText().toString());
    }

    @Test
    public void removeOneItemInList() {
        Feed feed = new Feed() {
            @Nullable
            @Override
            public String getLink() {
                return null;
            }

            @Nullable
            @Override
            public Date getPublicationDate() {
                return null;
            }

            @NonNull
            @Override
            public String getTitle() {
                return "new item";
            }

            @Nullable
            @Override
            public String getDescription() {
                return "new descritpion";
            }

            @Nullable
            @Override
            public String getCopyright() {
                return null;
            }

            @Nullable
            @Override
            public String getImageLink() {
                return null;
            }

            @Nullable
            @Override
            public String getAuthor() {
                return null;
            }

            @NonNull
            @Override
            public List<? extends Item> getItems() {
                return null;
            }
        };
        channelAdapter.add(feed);
        channelAdapter.remove(feed);
        assertEquals(channelAdapter.getItemCount(), 3);
    }

    @Test
    public void sendToAdapterAnEmptyList() {
        ChannelAdapter channelAdapter = new ChannelAdapter(new ArrayList<Feed>());
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
