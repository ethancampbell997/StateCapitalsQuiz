package edu.uga.cs.statecapitalsquiz;

public class States {

    private long   id;
    private String state;
    private String capital;
    private String city2;
    private String city3;
    private int statehood;
    private int capitalSince;
    private int rank;

    public States()
    {
        this.id = -1;
        this.state = null;
        this.capital = null;
        this.city2 = null;
        this.city3 = null;
        this.statehood = -1;
        this.capitalSince = -1;
        this.rank = -1;
    }

    public States( String state, String capital, String city2, String city3, int statehood, int capitalSince, int rank ) {
        this.id = -1;  // the primary key id will be set by a setter method
        this.state = state;
        this.capital = capital;
        this.city2 = city2;
        this.city3 = city3;
        this.statehood = statehood;
        this.capitalSince = capitalSince;
        this.rank = rank;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getCapital()
    {
        return capital;
    }

    public void setCapital(String capital)
    {
        this.capital = capital;
    }

    public String getCity2()
    {
        return city2;
    }

    public void setCity2(String city2)
    {
        this.city2 = city2;
    }

    public String getCity3()
    {
        return city3;
    }

    public void setCity3(String city3)
    {
        this.city3 = city3;
    }

    public int getStatehood()
    {
        return statehood;
    }

    public void setStatehood(int statehood)
    {
        this.statehood = statehood;
    }

    public int getCapitalSince()
    {
        return capitalSince;
    }

    public void setCapitalSince(int capitalSince)
    {
        this.capitalSince = capitalSince;
    }

    public int getRank()
    {
        return rank;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }

    public String toString()
    {
        return id + ": " + state + " " + capital + " " + city2 + " " + city3 + " "
                + statehood + " " + capitalSince + " " + rank;
    }
}
