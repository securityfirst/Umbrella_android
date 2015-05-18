package org.secfirst.umbrella.models;


public class FeedItem {

    private String title;
    private String body;
    private String url;
    private long date;

    public FeedItem() {
    }

    public FeedItem(String title, String body, String url) {
        this.title = title;
        this.body = body;
        this.url = url;
    }

    public FeedItem(String title, String heading, String body, String url) {
        this.title = title;
        this.body = body;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
