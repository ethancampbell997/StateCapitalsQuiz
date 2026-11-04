package edu.uga.cs.statecapitalsquiz;

public class QuizResult {
    private long id;
    private String date;
    private int result;

    public QuizResult() {
        this.id = -1;
        this.date = null;
        this.result = -1;
    }
    //constructor
    public QuizResult(String date, int result) {
        this.id = -1;
        this.date = date;
        this.result = result;
    }
    //getters and setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }
    @Override
    public String toString() {
        return id + ": " + date + " " + result;
    }
}
