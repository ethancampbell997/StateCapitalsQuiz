package edu.uga.cs.statecapitalsquiz;

public class ExtendedQuiz {
    private String city1;
    private String city2;
    private String city3;
    private Integer numCorrectAnswer;

    private String state;
    private String capital;
    private int statehood;
    private int capitalSince;
    private int rank;

    public ExtendedQuiz(String city1, String city2, String city3,
                         Integer numCorrectAnswer, String state,
                         String capital, int statehood,
                         int capitalSince, int rank) {

        this.city1 = city1;
        this.city2 = city2;
        this.city3 = city3;
        this.numCorrectAnswer = numCorrectAnswer;
        this.state = state;
        this.capital = capital;
        this.statehood = statehood;
        this.capitalSince = capitalSince;
        this.rank = rank;
    }

    public String getCity1() {
        return city1;
    }

    public String getCity2() {
        return city2;
    }

    public String getCity3() {
        return city3;
    }

    public Integer getNumCorrectAnswer() {
        return numCorrectAnswer;
    }

    public String getState() {
        return state;
    }

    public String getCapital() {
        return capital;
    }

    public int getStatehood() {
        return statehood;
    }

    public int getCapitalSince() {
        return capitalSince;
    }

    public int getRank() {
        return rank;
    }
}
