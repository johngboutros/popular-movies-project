package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Switch;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by john on 04/03/18.
 * <p>
 * Utilities to be used to communicate with the TMDb API.
 */

public class TMDbUtils {

    private static final String TAG = TMDbUtils.class.getSimpleName();

    // API Key (v3 auth)
    private static final String API_KEY = "<YOUR_API_KEY>";

    // API base URL
    private static final String API_BASE_URL = "http://api.themoviedb.org/3";

    // API common params
    private static final String API_KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";

    // Popular movies path
    // Example API request:
    // http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
    private static final String POPULAR_PATH = "/movie/popular";

    // Top rated movies path
    private static final String TOP_RATED_PATH = "/movie/top_rated";

    // Movie path
    // Example API request:
    // https://api.themoviedb.org/3/movie/550?api_key=[YOUR_API_KEY]
    private static final String MOVIE_PATH = "/movie/%s";


    // Discover path
    // Example API requests:
    // https://api.themoviedb.org/3/discover/movie?api_key=<<api_key>>&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1
    // https://api.themoviedb.org/3/discover/movie?api_key=<<api_key>>&language=en-US&sort_by=popularity.desc&page=1
    private static final String DISCOVER_PATH = "/discover/movie";

    // Discover params
    private static final String LANGUAGE_PARAM = "language";
    private static final String SORT_BY_PARAM = "sort_by";

    // Allowed SORT_BY Values:
    // popularity.asc, popularity.desc, release_date.asc, release_date.desc, revenue.asc,
    // revenue.desc, primary_release_date.asc, primary_release_date.desc, original_title.asc,
    // original_title.desc, vote_average.asc, vote_average.desc, vote_count.asc, vote_count.desc
    private static final String SORT_BY_ASC = ".asc";
    private static final String SORT_BY_DESC = ".desc";
    private static final String SORT_BY_POPULARITY = "popularity";
    private static final String SORT_BY_RELEASE_DATE = "release_date";
    private static final String SORT_BY_REVENUE = "revenue";
    private static final String SORT_BY_PRIMARY_RELEASE_DATE = "primary_release_date";
    private static final String SORT_BY_PRIMARY_ORIGINAL_TITLE = "original_title";
    private static final String SORT_BY_VOTE_AVERAGE = "vote_average";
    private static final String SORT_BY_VOTE_COUNT = "vote_count";

    /**
     * Movies list sort direction options
     */
    public enum SortDirection {
        DESC, ASC
    }

    /**
     * Movies list sort options
     */
    public enum SortBy {
        POPULARITY, RELEASE_DATE, REVENUE, PRIMARY_RELEASE_DATE, PRIMARY_ORIGINAL_TITLE,
        VOTE_AVERAGE, VOTE_COUNT
    }

    /**
     * Builds a value for SORT_BY param
     *
     * @param sortBy        @{@link SortBy} sort option
     * @param sortDirection @{@link SortDirection} sort direction
     * @return a string value for SORT_BY param
     */
    private static String buildSortOption(@NonNull SortBy sortBy, @NonNull SortDirection sortDirection) {

        String sortOption = "";

        switch (sortBy) {
            case POPULARITY:
                sortOption = SORT_BY_POPULARITY;
                break;
            case RELEASE_DATE:
                sortOption = SORT_BY_RELEASE_DATE;
                break;
            case REVENUE:
                sortOption = SORT_BY_REVENUE;
                break;
            case PRIMARY_RELEASE_DATE:
                sortOption = SORT_BY_PRIMARY_RELEASE_DATE;
                break;
            case PRIMARY_ORIGINAL_TITLE:
                sortOption = SORT_BY_PRIMARY_ORIGINAL_TITLE;
                break;
            case VOTE_AVERAGE:
                sortOption = SORT_BY_VOTE_AVERAGE;
                break;
            case VOTE_COUNT:
                sortOption = SORT_BY_VOTE_COUNT;
                break;
        }

        switch (sortDirection) {
            case DESC:
                sortOption += SORT_BY_DESC;
                break;
            case ASC:
                sortOption += SORT_BY_ASC;
                break;
        }

        return sortOption;
    }

    /**
     * Builds a @{@link URL} object for a discovery movies list request given a sort option as
     * a @{@link SortBy} parameter with a default sort direction based on the given sort option
     *
     * @param sortBy sort option
     * @return url for a discovery movies list request
     */
    public static URL buildDiscoveryUrl(@NonNull SortBy sortBy) {

        // Default sort direction
        SortDirection sortDirection;
        if (sortBy == SortBy.PRIMARY_ORIGINAL_TITLE) {
            sortDirection = SortDirection.ASC;
        } else {
            sortDirection = SortDirection.DESC;
        }

        Uri uri = Uri.parse(API_BASE_URL + DISCOVER_PATH).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(SORT_BY_PARAM, buildSortOption(sortBy, sortDirection))
                .build();

        return buildUrl(uri);
    }

    /**
     * Returns a URL to a movie GET request.
     *
     * @param movieId the movie ID
     * @return URL to a movie GET request
     */
    public static URL buildMovieUrl(@NonNull Integer movieId) {

        Uri uri = Uri.parse(API_BASE_URL + String.format(MOVIE_PATH, movieId.toString()))
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        return buildUrl(uri);
    }

    /**
     * Builds a URL for a given URI.
     *
     * @param uri the URI to build a URL from
     * @return URL for the given URI
     */
    private static URL buildUrl(@NonNull Uri uri) {
        try {
            URL url = new URL(uri.toString());
            Log.v(TAG, "URL built: " + url);
            return url;
        } catch (MalformedURLException e) {
            Log.e(TAG, "ERROR building URL (" + uri.toString() + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
