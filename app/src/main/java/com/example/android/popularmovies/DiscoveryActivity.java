package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.GsonRequest;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryActivity extends AppCompatActivity {

    private static final String TAG = DiscoveryActivity.class.getSimpleName();

    @BindView(R.id.discovery_list_rv)
    RecyclerView discoveryRecyclerView;

    @BindInt(R.integer.discovery_grid_columns)
    int gridColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discovery);
        ButterKnife.bind(this);

        discoveryRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
        discoveryRecyclerView.setLayoutManager(layoutManager);

//        DiscoveryAdapter adapter = new DiscoveryAdapter(this, Arrays.asList(testMovies));
//        discoveryRecyclerView.setAdapter(adapter);

        // TODO Delete this test code
        // Testing Volley
        Request movieRequest
                = new GsonRequest<Movie.Page>(Request.Method.GET,
                TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.POPULARITY).toString(),
                null,
                Movie.Page.class,
                null,
                new Response.Listener<Movie.Page>() {
                    @Override
                    public void onResponse(Movie.Page moviePage) {
//                        Log.d(TAG, "Response: " + response.toString());
//                        Movie movie = new Gson().fromJson(response.toString(), Movie.class);
//                        Log.d(TAG, "Movie: " + movie);
                        Log.d(TAG, "Movie Page: " + moviePage);

                        DiscoveryAdapter adapter
                                = new DiscoveryAdapter(DiscoveryActivity.this
                                , moviePage.getResults());

                        discoveryRecyclerView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DiscoveryActivity.this,
                        "Couldn't load movie", Toast.LENGTH_LONG).show();
                Log.e(TAG, "VolleyError: " + error.getMessage());
            }
        });

        NetworkUtils.get(this).addToRequestQueue(movieRequest);
        // Testing Volley end
    }
}
