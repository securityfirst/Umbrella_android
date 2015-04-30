package org.secfirst.umbrella.models.Relief;

public class Data
{
    private String id;

    private Fields fields;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
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
        return "ClassPojo [id = "+id+", fields = "+fields+"]";
    }
}