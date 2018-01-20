package org.secfirst.umbrella.rss.api;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public class Channel {

    private String title;
    private String description;
    private String image;
    private List<Article> articles;


    public Channel(String title, String description, @NonNull List<Article> articles) {
        this.title = title;
        this.description = description;
        this.articles = articles;
    }

    public Channel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", articles=" + articles +
                '}';
    }
}
