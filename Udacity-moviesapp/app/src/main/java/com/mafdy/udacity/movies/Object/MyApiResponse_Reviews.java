package com.mafdy.udacity.movies.Object;

/**
 * Created by SBP on 03/05/2018.
 */

public class MyApiResponse_Reviews {

    private int id;
    private MyReview[] results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MyReview[] getResults() {
        return results;
    }

    public void setResults(MyReview[] results) {
        this.results = results;
    }
}
