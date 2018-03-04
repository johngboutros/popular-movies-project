package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.popularmovies.data.Movie;

import java.util.Arrays;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryActivity extends AppCompatActivity {

    @BindView(R.id.discovery_list_rv)
    RecyclerView discoveryRecyclerView;

    @BindInt(R.integer.discovery_grid_columns)
    int gridColumns;

    private final Movie testMovies[] = {
            new Movie("https://api.learn2crack.com/android/images/donut.png"),
            new Movie("https://api.learn2crack.com/android/images/eclair.png"),
            new Movie("https://api.learn2crack.com/android/images/froyo.png"),
            new Movie("https://api.learn2crack.com/android/images/ginger.png"),
            new Movie("https://api.learn2crack.com/android/images/honey.png"),
            new Movie("https://api.learn2crack.com/android/images/icecream.png"),
            new Movie("https://api.learn2crack.com/android/images/jellybean.png"),
            new Movie("https://api.learn2crack.com/android/images/kitkat.png"),
            new Movie("https://api.learn2crack.com/android/images/lollipop.png"),
            new Movie("https://api.learn2crack.com/android/images/marshmallow.png")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_discovery);
        ButterKnife.bind(this);

        discoveryRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
        discoveryRecyclerView.setLayoutManager(layoutManager);

        DiscoveryAdapter adapter = new DiscoveryAdapter(this, Arrays.asList(testMovies));
        discoveryRecyclerView.setAdapter(adapter);
    }
}
