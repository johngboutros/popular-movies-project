package com.example.android.popularmovies.data;

/**
 * Created by john on 03/03/18.
 */

public class Movie {

    private String url;

    public Movie(String url) {
        setUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
