package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.android.popularmovies.DiscoveryAdapter.MovieClickListener;
import com.example.android.popularmovies.components.PaginationScrollListener;
import com.example.android.popularmovies.data.FavoritesDao;
import com.example.android.popularmovies.data.FavoritesDatabase;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.GsonRequest;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbUtils;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryActivity extends AppCompatActivity {

    private static final String TAG = DiscoveryActivity.class.getSimpleName();

    /**
     * Available sort options provided by the adapter
     */
    public enum SortOption {
        POPULARITY, TOP_RATED, RELEASE_DATE, REVENUE, FAVORITS
    }

    @BindView(R.id.discovery_list_rv)
    RecyclerView discoveryRecyclerView;

    @BindInt(R.integer.discovery_grid_columns)
    int gridColumns;

    @BindString(R.string.pref_discovery_sort_key)
    String sortPrefKey;

    @BindString(R.string.pref_discovery_sort_popularity)
    String sortPrefPopularity;

    @BindString(R.string.pref_discovery_sort_top_rated)
    String sortPrefTopRated;

    @BindString(R.string.pref_discovery_sort_release_date)
    String sortPrefReleaseDate;

    @BindString(R.string.pref_discovery_sort_revenue)
    String sortPrefRevenue;

    @BindString(R.string.pref_discovery_sort_favorites)
    String sortPrefFavorites;

    @BindString(R.string.pref_discovery_sort_default)
    String sortPrefDefault;

    // Discovered pages count
    private int pageCount;

    // Total result pages count
    private int totalPageCount;

    // Loading flag
    private boolean isLoading;

    // Current SortOption
    private final SortOption defaultSortOption = SortOption.POPULARITY;

    // Current SortOption
    private SortOption currentSortOption = defaultSortOption;


    // Saved instance state Bundle keys
    private final static String LAYOUT_STATE_BUNDLE_KEY = "layout_state";
    private final static String ADAPTER_STATE_BUNDLE_KEY = "adapter_state";
    private final static String TITLE_BUNDLE_KEY = "title";

    // Discovery Adapter
    private DiscoveryAdapter discoveryAdapter;

    // MovieClickListener
    private MovieClickListener movieClickListener;

    // Scroll listener
    private ScrollListener scrollListener;

    // Favorites DAO
    private FavoritesDao favoritesDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discovery);
        ButterKnife.bind(this);

        favoritesDao = FavoritesDatabase.get(this).favoritesDao();

        discoveryRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
        discoveryRecyclerView.setLayoutManager(layoutManager);

        discoveryAdapter = new DiscoveryAdapter(this);
        discoveryRecyclerView.setAdapter(discoveryAdapter);

        if (savedInstanceState == null) {
            loadSortPreferences();
        } else {
            Parcelable adapterState = savedInstanceState.getParcelable(ADAPTER_STATE_BUNDLE_KEY);
            Parcelable layoutState = savedInstanceState.getParcelable(LAYOUT_STATE_BUNDLE_KEY);
            String title = savedInstanceState.getString(TITLE_BUNDLE_KEY);

            restoreInstanceState(adapterState);
            discoveryRecyclerView.getLayoutManager().onRestoreInstanceState(layoutState);
            setTitle(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (movieClickListener == null) {
            movieClickListener = new MovieClickListener() {
                @Override
                public void onMovieClicked(Movie movie) {
                    // launch MovieDetails Activity
                    Intent intent = new Intent(DiscoveryActivity.this,
                            MovieDetailsActivity.class);
                    intent.putExtra(MovieDetailsActivity.MOVIE_EXTRA_PARAM, Parcels.wrap(movie));

                    startActivity(intent);
                }
            };
            discoveryAdapter.addMovieClickListener(movieClickListener);
        }

        if (scrollListener == null) {
            scrollListener = new ScrollListener(
                    (LinearLayoutManager) discoveryRecyclerView.getLayoutManager());
            discoveryRecyclerView.addOnScrollListener(scrollListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (movieClickListener != null) {
            discoveryAdapter.removeMovieClickListener(movieClickListener);
            movieClickListener = null;
        }

        if (scrollListener != null) {
            discoveryRecyclerView.removeOnScrollListener(scrollListener);
            scrollListener = null;
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_discovery, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_sort_by_popularity:
                saveSortPreferences(R.string.pref_discovery_sort_popularity);
                break;

            case R.id.menu_sort_by_top_rated:
                saveSortPreferences(R.string.pref_discovery_sort_top_rated);
                break;

            case R.id.menu_sort_by_release_date:
                saveSortPreferences(R.string.pref_discovery_sort_release_date);
                break;

            case R.id.menu_sort_by_revenue:
                saveSortPreferences(R.string.pref_discovery_sort_revenue);
                break;

            case R.id.menu_favorites:
                saveSortPreferences(R.string.pref_discovery_sort_favorites);
                break;

        }

        loadSortPreferences();

        return true;
    }

    private void saveSortPreferences(int preferenceId) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(sortPrefKey, getString(preferenceId));
        editor.apply();

    }

    private void loadSortPreferences() {
        String preferenceValue = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(sortPrefKey, sortPrefDefault);

        if (sortPrefPopularity.equals(preferenceValue)) {
            setTitle(R.string.popular_movies);
            discoverMore(SortOption.POPULARITY);
        } else if (sortPrefTopRated.equals(preferenceValue)) {
            setTitle(R.string.top_rated_movies);
            discoverMore(SortOption.TOP_RATED);
        } else if (sortPrefReleaseDate.equals(preferenceValue)) {
            setTitle(R.string.recently_released_movies);
            discoverMore(SortOption.RELEASE_DATE);
        } else if (sortPrefRevenue.equals(preferenceValue)) {
            setTitle(R.string.highest_grossing_movies);
            discoverMore(SortOption.REVENUE);
        } else if (sortPrefFavorites.equals(preferenceValue)) {
            setTitle(R.string.menu_favorites);
            discoverMore(SortOption.FAVORITS);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Parcelable adapterState = saveInstanceState();
        Parcelable layoutState = discoveryRecyclerView.getLayoutManager().onSaveInstanceState();

        outState.putParcelable(ADAPTER_STATE_BUNDLE_KEY, adapterState);
        outState.putParcelable(LAYOUT_STATE_BUNDLE_KEY, layoutState);
        outState.putString(TITLE_BUNDLE_KEY, String.valueOf(getTitle()));
    }

    /**
     * Loads more movies using the current sort option.
     */
    public void discoverMore() {
        discoverMore(null);
    }

    /**
     * Loads more movies using the provided sort option.
     *
     * @param sortOption
     */
    public void discoverMore(SortOption sortOption) {

        if (sortOption != null && !currentSortOption.equals(sortOption)) {
            this.currentSortOption = sortOption;
            if (isLoading) {
                isLoading = false;
                discoveryAdapter.stopLoading();
            }
            discoveryAdapter.clear();
            pageCount = 0;
            totalPageCount = 0;
        }

        Integer page = pageCount > 0 ? pageCount + 1 : null;

        String url = null;

        switch (this.currentSortOption) {
            case FAVORITS:
                loadFavorites();
                break;
            case POPULARITY:
                url = TMDbUtils.buildPopularMoviesURL(page).toString();
                loadUrl(url);
                break;
            case TOP_RATED:
                url = TMDbUtils.buildTopRatedMoviesURL(page).toString();
                loadUrl(url);
                break;
            case RELEASE_DATE:
                url = TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.RELEASE_DATE, page).toString();
                loadUrl(url);
                break;
            case REVENUE:
                url = TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.REVENUE, page).toString();
                loadUrl(url);
                break;
            default:
                url = TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.POPULARITY, page).toString();
                loadUrl(url);
        }
    }

    private void loadUrl(String url) {
        if (!isLoading) {
            isLoading = true;
            discoveryAdapter.startLoading();
        }

        Request movieRequest
                = new GsonRequest<Movie.Page>(Request.Method.GET,
                url,
                null,
                Movie.Page.class,
                null,
                new Response.Listener<Movie.Page>() {
                    @Override
                    public void onResponse(Movie.Page moviePage) {
                        if (isLoading) {
                            isLoading = false;
                            discoveryAdapter.stopLoading();
                        }
                        Log.d(TAG, "Movie Page: " + moviePage);

                        pageCount = moviePage.getPage();
                        totalPageCount = moviePage.getTotalPages();
                        discoveryAdapter.addAll(moviePage.getResults());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (isLoading) {
                            isLoading = false;
                            discoveryAdapter.stopLoading();
                        }

                        Toast.makeText(DiscoveryActivity.this,
                                getString(R.string.discovery_load_error), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "VolleyError: " + error.getMessage());
                    }
                });

        NetworkUtils.get(DiscoveryActivity.this).addToRequestQueue(movieRequest);
    }

    private void loadFavorites() {

        if (!isLoading) {
            isLoading = true;
            discoveryAdapter.startLoading();
        }

        // Using Cursor by Room
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                final Cursor cursor = favoritesDao.getAllCursor();
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (!currentSortOption.equals(SortOption.FAVORITS))
//                            return;
//
//                        if (isLoading) {
//                            isLoading = false;
//                            discoveryAdapter.stopLoading();
//                        }
//
//                        Log.d(TAG, "Favorites loaded, size: " + cursor.getCount());
//
//                        while (cursor.moveToNext()) {
//
//                            Movie movie = new Movie(cursor);
//
//                            discoveryAdapter.add(movie);
//                        }
//                    }
//                });
//            }
//        });

        // Using Room's LiveData
        LiveData<List<Movie>> favorites = favoritesDao.getAllAsync();

        favorites.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {

                if (!currentSortOption.equals(SortOption.FAVORITS))
                    return;

                if (isLoading) {
                    isLoading = false;
                    discoveryAdapter.stopLoading();
                }

                Log.d(TAG, "Favorites loaded, size: " + movies.size());

                discoveryAdapter.setMovies(movies);

            }
        });
    }

    /**
     * Generates the adapter's state as a {@link Parcelable}
     *
     * @return the adapter's instance state
     */
    public Parcelable saveInstanceState() {

        SavedInstanceState state = new SavedInstanceState();
        state.movies = discoveryAdapter.getMovies();
        state.pageCount = pageCount;
        state.totalPageCount = totalPageCount;
        state.isLoading = this.isLoading;
        state.currentSortOption = currentSortOption;

        return Parcels.wrap(state);
    }

    /**
     * Restores the adapter's state using a {@link Parcelable} generated by
     * saveInstanceState()
     *
     * @param savedInstanceState {@link Parcelable} generated by saveInstanceState()
     */
    public void restoreInstanceState(Parcelable savedInstanceState) {
        SavedInstanceState state = Parcels.unwrap(savedInstanceState);

        // NOTE: setting the List directly inside the adapter works as well!
        discoveryAdapter.addAll(state.movies);
        this.pageCount = state.pageCount;
        this.totalPageCount = state.totalPageCount;
        this.isLoading = state.isLoading;
        this.currentSortOption = state.currentSortOption;
    }

    /**
     * A class to save the adapter's state
     */
    @Parcel
    static class SavedInstanceState {
        // Discovered movies list
        List<Movie> movies = new ArrayList<Movie>();
        // Discovered pages count
        int pageCount;
        // Total result pages count
        int totalPageCount;
        // Loading flag
        boolean isLoading;
        // Current SortOption
        SortOption currentSortOption;
    }

    /**
     * Custom Pagination {@link RecyclerView.OnScrollListener} instance to be set to the {@link RecyclerView}.
     */
    private class ScrollListener extends PaginationScrollListener {

        /**
         * Initializes a new ScrollListener with an Adapter and a LayoutManager
         *
         * @param layoutManager {@link RecyclerView}'s LayoutManager
         */
        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        protected void loadMoreItems() {
            discoverMore();
        }

        @Override
        public int getTotalPageCount() {
            return totalPageCount;
        }

        @Override
        public boolean isLastPage() {
            return pageCount >= getTotalPageCount();
        }

        @Override
        public boolean isLoading() {
            return isLoading;
        }
    }
}
