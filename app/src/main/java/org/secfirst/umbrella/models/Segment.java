package org.secfirst.umbrella.models;

import com.orm.SugarRecord;

public class Segment extends SugarRecord<Segment> {
    private String title;
    private String subtitle;
    private String body;
    private int category;

    public Segment(){}

    public Segment(String title, String subtitle, String body, int category) {
        this.title = title;
        this.subtitle = subtitle;
        this.body = body;
        this.category = category;
    }

    public Segment(String body, int category) {
        this.title = "";
        this.subtitle = "";
        this.body = body;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
