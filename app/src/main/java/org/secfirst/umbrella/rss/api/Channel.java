package org.secfirst.umbrella.rss.api;

import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public class Channel {

    private String title;
    private String description;
    private List<Article> articles;


    public Channel(String title, String description, List<Article> articles) {
        this.title = title;
        this.description = description;
        this.articles = articles;
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

    @Override
    public String toString() {
        return "Channel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", articles=" + articles +
                '}';
    }
}
