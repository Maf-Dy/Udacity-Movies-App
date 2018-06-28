package com.mafdy.udacity.movies.Object;

/**
 * Created by SBP on 03/05/2018.
 */

public class MyApiResponse_Videos {

    private int id;

    private MyVideo[] results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MyVideo[] getResults() {
        return results;
    }

    public void setResults(MyVideo[] results) {
        this.results = results;
    }
}
