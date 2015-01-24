package org.secfirst.umbrella.models;


public class FeedItem {

    private String title;
    private String heading;
    private String body;
    private String url;

    public FeedItem(String title, String heading, String body, String url) {
        this.title = title;
        this.heading = heading;
        this.body = body;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
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
}
