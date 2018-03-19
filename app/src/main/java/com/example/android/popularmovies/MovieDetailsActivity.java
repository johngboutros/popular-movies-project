package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

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

        setTitle(movie.getTitle());

        String posterURL = TMDbUtils.buildPosterURL(movie.getPosterPath(), TMDbUtils.PosterSize.W185);

        Picasso.with(this).load(posterURL)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.bg_movie_thumb)
                .into(imageDisplay);

        SimpleDateFormat dateFormat = new SimpleDateFormat(YEAR_FORMAT);

        yearDisplay.setText(dateFormat.format(TMDbUtils.parseDate(movie.getReleaseDate())));

        rateDisplay.setText(String.format(rateFormat, movie.getVoteAverage()));

        overviewDisplay.setText(movie.getOverview());
    }
}
