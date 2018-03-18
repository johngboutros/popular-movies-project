package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popularmovies.data.Movie;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryActivity extends AppCompatActivity {

    private static final String TAG = DiscoveryActivity.class.getSimpleName();

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


    @BindString(R.string.pref_discovery_sort_default)
    String sortPrefDefault;


    // Discovery Adapter
    private DiscoveryAdapter discoveryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discovery);
        ButterKnife.bind(this);

        discoveryRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
        discoveryRecyclerView.setLayoutManager(layoutManager);

        discoveryAdapter = new DiscoveryAdapter(this);
        discoveryRecyclerView.setAdapter(discoveryAdapter);

        // TODO add/remove on start/stop
        // TODO Move to Adapter.onAttachRecyclerView()?
        discoveryRecyclerView.addOnScrollListener(
                new DiscoveryAdapter.ScrollListener(discoveryAdapter, layoutManager));

        // TODO add/remove on start/stop
        discoveryAdapter.addMovieClickListener(new DiscoveryAdapter.MovieClickListener() {
            @Override
            public void onMovieClicked(Movie movie) {
                // TODO launch MovieDetails Avtivity
//                Toast.makeText(DiscoveryActivity.this, movie.getTitle(),
//                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DiscoveryActivity.this,
                        MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.MOVIE_EXTRA_PARAM, movie);

                startActivity(intent);
            }
        });

        loadSortPreferences();
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
            discoveryAdapter.discoverMore(DiscoveryAdapter.SortOption.POPULARITY);
        } else if (sortPrefTopRated.equals(preferenceValue)) {
            setTitle(R.string.top_rated_movies);
            discoveryAdapter.discoverMore(DiscoveryAdapter.SortOption.TOP_RATED);
        } else if (sortPrefReleaseDate.equals(preferenceValue)) {
            setTitle(R.string.recently_released_movies);
            discoveryAdapter.discoverMore(DiscoveryAdapter.SortOption.RELEASE_DATE);
        } else if (sortPrefRevenue.equals(preferenceValue)) {
            setTitle(R.string.highest_grossing_movies);
            discoveryAdapter.discoverMore(DiscoveryAdapter.SortOption.REVENUE);
        }

    }
}
