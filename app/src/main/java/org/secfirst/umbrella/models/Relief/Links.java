package org.secfirst.umbrella.models.Relief;

public class Links
{
    private Self self;

    private Collection collection;

    public Self getSelf ()
    {
        return self;
    }

    public void setSelf (Self self)
    {
        this.self = self;
    }

    public Collection getCollection ()
    {
        return collection;
    }

    public void setCollection (Collection collection)
    {
        this.collection = collection;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [self = "+self+", collection = "+collection+"]";
    }
}