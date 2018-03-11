package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.android.popularmovies.components.PaginationScrollListener;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.GsonRequest;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryActivity extends AppCompatActivity {

    private static final String TAG = DiscoveryActivity.class.getSimpleName();

    @BindView(R.id.discovery_list_rv)
    RecyclerView discoveryRecyclerView;

    @BindInt(R.integer.discovery_grid_columns)
    int gridColumns;

    // Discovery Adapter
    private DiscoveryAdapter discoveryAdapter;
    // Discovery custom RecyclerView OnScrollListener
    private PaginationScrollListener scrollListener;
    // Discovered movies list
    private List<Movie> discoveredMovies = new ArrayList<Movie>();
    // Discovered pages count
    private int pageCount;
    // Total result pages count
    private int totalPageCount;
    // Loading flag
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discovery);
        ButterKnife.bind(this);

//        discoveryRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
        discoveryRecyclerView.setLayoutManager(layoutManager);

        discoveryAdapter = new DiscoveryAdapter(this, discoveredMovies);
//        discoveryRecyclerView.setAdapter(discoveryAdapter);
        discoveryRecyclerView.addOnScrollListener(new DiscoveryScrollListener(layoutManager));

        discoverMore();
    }

    private void discoverMore() {

        Integer page = pageCount > 0 ? pageCount + 1 : null;
        isLoading = true;

        Request movieRequest
                = new GsonRequest<Movie.Page>(Request.Method.GET,
                TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.POPULARITY, page).toString(),
                null,
                Movie.Page.class,
                null,
                new Response.Listener<Movie.Page>() {
                    @Override
                    public void onResponse(Movie.Page moviePage) {
                        isLoading = false;
                        Log.d(TAG, "Movie Page: " + moviePage);

                        discoveredMovies.addAll(moviePage.getResults());
                        pageCount = moviePage.getPage();
                        totalPageCount = moviePage.getTotalPages();

                        discoveryRecyclerView.setAdapter(discoveryAdapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        Toast.makeText(DiscoveryActivity.this,
                                "Couldn't load movies", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "VolleyError: " + error.getMessage());
                    }
                });

        NetworkUtils.get(this).addToRequestQueue(movieRequest);
    }

    private class DiscoveryScrollListener extends PaginationScrollListener {

        public DiscoveryScrollListener(LinearLayoutManager layoutManager) {
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
