package edu.uga.cs.statecapitalsquiz;

public class Quiz {

    private long   id;
    private long stateId;
    private String city1;
    private String city2;
    private String city3;
    private Integer numCorrectAnswer;

    public Quiz()
    {
        this.id = -1;
        this.stateId = -1;
        this.city1 = null;
        this.city2 = null;
        this.city3 = null;
        this.numCorrectAnswer = null;
    }

    public Quiz( long stateId, String city1, String city2, String city3, Integer numCorrectAnswer) {
        this.id = -1;  // the primary key id will be set by a setter method
        this.stateId = stateId;
        this.city1 = city1;
        this.city2 = city2;
        this.city3 = city3;
        this.numCorrectAnswer = numCorrectAnswer;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getStateId()
    {
        return stateId;
    }

    public void setStateId(long stateId)
    {
        this.stateId = stateId;
    }

    public String getCity1()
    {
        return city1;
    }

    public void setCity1(String capital)
    {
        this.city1 = capital;
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

    public Integer getNumCorrectAnswer()
    {
        return numCorrectAnswer;
    }

    public void setNumCorrectAnswer(Integer numCorrectAnswer)
    {
        this.numCorrectAnswer = numCorrectAnswer;
    }

    public String toString()
    {
        return id + ": " + stateId + " " + city1 + " " + city2 + " " + city3 + " " + numCorrectAnswer;
    }
}
