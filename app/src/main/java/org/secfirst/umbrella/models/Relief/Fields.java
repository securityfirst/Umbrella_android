package org.secfirst.umbrella.models.Relief;

import com.google.gson.annotations.SerializedName;

public class Fields
{
    private String id;

    private String[] location;

    private String status;

    private String description;

    private String name;

    private String current;

    private String iso3;

    private String featured;

    @SerializedName("description-html") private String descriptionhtml;

    private String url;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String[] getLocation ()
    {
        return location;
    }

    public void setLocation (String[] location)
    {
        this.location = location;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCurrent ()
    {
        return current;
    }

    public void setCurrent (String current)
    {
        this.current = current;
    }

    public String getIso3 ()
    {
        return iso3;
    }

    public void setIso3 (String iso3)
    {
        this.iso3 = iso3;
    }

    public String getFeatured ()
    {
        return featured;
    }

    public void setFeatured (String featured)
    {
        this.featured = featured;
    }

    public String getDescriptionhtml ()
{
    return descriptionhtml;
}

    public void setDescriptionhtml (String descriptionhtml)
{
    this.descriptionhtml = descriptionhtml;
}

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", location = "+location+", status = "+status+", description = "+description+", name = "+name+", current = "+current+", iso3 = "+iso3+", featured = "+featured+", description-html = "+descriptionhtml+", url = "+url+"]";
    }
}