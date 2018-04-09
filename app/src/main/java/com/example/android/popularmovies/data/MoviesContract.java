package com.example.android.popularmovies.data;

import android.net.Uri;

/**
 * Created by john on 10/04/18.
 */

public class MoviesContract {

    // The authority, which is how your code knows which Content Provider to access
    static final String AUTHORITY = "com.example.android.popularmovies.data.MoviesProvider";

    // The base content URI = "content://" + <authority>
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    static final String PATH = "movies";

    static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();


    interface Columns {
        String UID = "uid";
        String TITLE = "title";
        String POSTER_PATH = "poster_path";
        String OVERVIEW = "overview";
        String RELEASE_DATE = "release_date";
        String VOTE_AVERAGE = "vote_average";
        String CREATION_DATE = "creation_date";
    }
}
