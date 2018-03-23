package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.utilities.GsonRequest;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    // Movie Intent Extra param
    public static final String MOVIE_EXTRA_PARAM = "movie";

    @BindView(R.id.movie_detail_image_iv)
    ImageView imageDisplay;

    @BindView(R.id.movie_detail_year_tv)
    TextView yearDisplay;


//    @BindView(R.id.movie_detail_duration_tv)
//    TextView durationDisplay;


    @BindView(R.id.movie_detail_rate_tv)
    TextView rateDisplay;


    @BindView(R.id.movie_detail_overview_tv)
    TextView overviewDisplay;

    @BindString(R.string.movie_detail_rate_format)
    String rateFormat;

    private static final String YEAR_FORMAT = "yyyy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_EXTRA_PARAM));

        // Load videos
        loadVideos(movie.getId());

        setTitle(movie.getTitle());

        if (TextUtils.isEmpty(movie.getPosterPath())) {

            imageDisplay.setImageDrawable(getResources().getDrawable(R.drawable.bg_movie_thumb));

        } else {

            String posterURL = TMDbUtils.buildPosterURL(movie.getPosterPath(), TMDbUtils.PosterSize.W185);

            Picasso.with(this).load(posterURL)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.bg_movie_thumb)
                    .into(imageDisplay);
        }

        if (!TextUtils.isEmpty(movie.getReleaseDate())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(YEAR_FORMAT);
            yearDisplay.setText(dateFormat.format(TMDbUtils.parseDate(movie.getReleaseDate())));
        }

        if (movie.getVoteAverage() > 0) {
            rateDisplay.setText(String.format(rateFormat, movie.getVoteAverage()));
        } else {
            rateDisplay.setText(R.string.movie_detail_unrated);
        }

        if (!TextUtils.isEmpty(movie.getOverview())) {
            overviewDisplay.setText(movie.getOverview());
        }
    }

    private void loadVideos(@NonNull Integer movieId) {

        String url = TMDbUtils.buildMovieVideosUrl(movieId).toString();

        // TODO show loading..

        Request movieRequest
                = new GsonRequest<Video.Page>(Request.Method.GET,
                url,
                null,
                Video.Page.class,
                null,
                new Response.Listener<Video.Page>() {
                    @Override
                    public void onResponse(Video.Page page) {

                        // TODO stop loading

                        Log.d(TAG, "Page: " + page);

                        // TODO display videos
                        Toast.makeText(MovieDetailsActivity.this,
                                "Page: " + page, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // TODO stop loading

                        Toast.makeText(MovieDetailsActivity.this,
                                "Couldn't load videos", Toast.LENGTH_LONG).show();

                        Log.e(TAG, "VolleyError: " + error.getMessage());
                    }
                });

        NetworkUtils.get(MovieDetailsActivity.this).addToRequestQueue(movieRequest);
    }
}
