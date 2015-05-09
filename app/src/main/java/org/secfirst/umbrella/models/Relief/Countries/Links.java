package org.secfirst.umbrella.models.Relief.Countries;

public class Links
{
    private Self self;

    public Self getSelf ()
    {
        return self;
    }

    public void setSelf (Self self)
    {
        this.self = self;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [self = "+self+"]";
    }
}