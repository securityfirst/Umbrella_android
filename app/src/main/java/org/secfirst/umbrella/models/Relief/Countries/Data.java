package org.secfirst.umbrella.models.Relief.Countries;

public class Data
{
    private String id;

    private String score;

    private String href;

    private Fields fields;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getScore ()
    {
        return score;
    }

    public void setScore (String score)
    {
        this.score = score;
    }

    public String getHref ()
    {
        return href;
    }

    public void setHref (String href)
    {
        this.href = href;
    }

    public Fields getFields ()
    {
        return fields;
    }

    public void setFields (Fields fields)
    {
        this.fields = fields;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", score = "+score+", href = "+href+", fields = "+fields+"]";
    }
}