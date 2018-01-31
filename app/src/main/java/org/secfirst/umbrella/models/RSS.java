package org.secfirst.umbrella.models;

import java.util.List;

/**
 * Created by dougl on 31/01/2018.
 */

public class RSS {

    private Feed feed;


    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public class Feed {

        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }

    public class Item {

        private String link;

        public void setLink(String link) {
            this.link = link;
        }

        public String getLink() {
            return link;
        }
    }
}
