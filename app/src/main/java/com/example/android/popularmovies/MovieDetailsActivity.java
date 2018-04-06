package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.android.popularmovies.data.FavoritesDao;
import com.example.android.popularmovies.data.FavoritesDatabase;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Video;
import com.example.android.popularmovies.utilities.GsonRequest;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    // Movie Intent Extra param
    public static final String MOVIE_EXTRA_PARAM = "movie";

    // Max displayed reviews count
    private static final int MAX_REVIEWS_COUNT = 5;

    @BindView(R.id.movie_detail_image_iv)
    ImageView imageDisplay;

    @BindView(R.id.movie_detail_year_tv)
    TextView yearDisplay;

    private static final String YEAR_FORMAT = "yyyy";

//    @BindView(R.id.movie_detail_duration_tv)
//    TextView durationDisplay;

    @BindView(R.id.movie_detail_rate_tv)
    TextView rateDisplay;

    @BindString(R.string.movie_detail_rate_format)
    String rateFormat;

    @BindView(R.id.movie_detail_overview_tv)
    TextView overviewDisplay;

    @BindView(R.id.movie_detail_trailers_separator_v)
    View trailersSeparator;

    @BindView(R.id.movie_detail_trailers_label_tv)
    View trailersLabel;

    @BindView(R.id.movie_detail_trailers_loading_pb)
    View trailersLoading;

    @BindView(R.id.movie_detail_trailers_container_ll)
    LinearLayout trailersContainer;

    @BindView(R.id.movie_detail_reviews_separator_v)
    View reviewsSeparator;

    @BindView(R.id.movie_detail_reviews_label_tv)
    View reviewsLabel;

    @BindView(R.id.movie_detail_reviews_loading_pb)
    View reviewsLoading;

    @BindView(R.id.movie_detail_reviews_container_ll)
    LinearLayout reviewsContainer;

    @BindView(R.id.movie_detail_favorite_btn)
    Button favoriteButton;

    private FavoritesDao favoritesDao;

    // Not null if this movie was saved as favorite
    private Movie favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        favoritesDao = FavoritesDatabase.get(this).favoritesDao();

        final Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_EXTRA_PARAM));

        // Load videos
        loadVideos(movie.getId());

        // Load reviews
        loadReviews(movie.getId());

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

        // Load favorite movie if exists
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                favorite = favoritesDao.get(movie.getId());

                // Setup favorite view if exists
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupFavorite(favorite != null);
                    }
                });

            }
        });
    }

    private void loadVideos(@NonNull Integer movieId) {

        String url = TMDbUtils.buildMovieVideosUrl(movieId).toString();

        // Start loading
        trailersLoading.setVisibility(View.VISIBLE);

        // Display trailers
        Request videosRequest
                = new GsonRequest<>(Request.Method.GET,
                url,
                null,
                Video.Page.class,
                null,
                new Response.Listener<Video.Page>() {
                    @Override
                    public void onResponse(Video.Page page) {

                        // Stop loading
                        trailersLoading.setVisibility(View.GONE);

                        Log.d(TAG, "Page: " + page);

                        if (page.getResults() != null && !page.getResults().isEmpty()) {

                            Iterator<Video> videoIterator = page.getResults().iterator();

                            while (videoIterator.hasNext()) {
                                final Video video = videoIterator.next();

                                View videoItem = getLayoutInflater()
                                        .inflate(R.layout.item_movie_details_video, trailersContainer,
                                                false);

                                TextView title = videoItem
                                        .findViewById(R.id.movie_detail_video_item_name_tv);
                                title.setText(video.getName());

                                if (!videoIterator.hasNext()) {
                                    View separator = videoItem
                                            .findViewById(R.id.movie_detail_video_item_separator_v);
                                    separator.setVisibility(View.INVISIBLE);
                                }

                                // Handle video click
                                videoItem.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (video.getSite() != null
                                                && video.getSite().toLowerCase().equals("youtube")) {

                                            Uri webpage = TMDbUtils.buildYoutubeUri(video.getKey());
                                            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(intent);
                                            }

                                        } else {
                                            Toast.makeText(MovieDetailsActivity.this,
                                                    getString(R.string.movie_detail_video_not_supported),
                                                    Toast.LENGTH_LONG).show();

                                            Log.e(TAG, "Video site not supported: " + video.getSite());
                                        }

                                    }
                                });

                                trailersContainer.addView(videoItem);
                            }
                        } else {
                            // Hide trailers view
                            hideTrailersView();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Stop loading
                        trailersLoading.setVisibility(View.GONE);

                        hideTrailersView();

                        Toast.makeText(MovieDetailsActivity.this,
                                getString(R.string.movie_detail_load_trailers_error),
                                Toast.LENGTH_LONG).show();

                        Log.e(TAG, "VolleyError: " + error.getMessage());
                    }
                });

        NetworkUtils.get(MovieDetailsActivity.this).addToRequestQueue(videosRequest);
    }

    private void loadReviews(@NonNull Integer movieId) {

        String url = TMDbUtils.buildMovieReviewsUrl(movieId).toString();

        // Start loading
        reviewsLoading.setVisibility(View.VISIBLE);

        // Display reviews
        Request reviewsRequest
                = new GsonRequest<>(Request.Method.GET,
                url,
                null,
                Review.Page.class,
                null,
                new Response.Listener<Review.Page>() {
                    @Override
                    public void onResponse(Review.Page page) {

                        // Stop loading
                        reviewsLoading.setVisibility(View.GONE);

                        Log.d(TAG, "Page: " + page);

                        if (page.getResults() != null && !page.getResults().isEmpty()) {

                            Iterator<Review> reviewIterator = page.getResults().iterator();

                            int displayedCount = 0;
                            while (reviewIterator.hasNext()) {

                                final Review review = reviewIterator.next();

                                // Skip empty reviews
                                if (TextUtils.isEmpty(review.getContent())) continue;

                                View reviewItem = getLayoutInflater()
                                        .inflate(R.layout.item_movie_details_review, reviewsContainer,
                                                false);

                                String author = TextUtils.isEmpty(review.getAuthor()) ?
                                        getString(R.string.movie_detail_reviews_unknown_author) :
                                        review.getAuthor();

                                TextView authorTv = reviewItem
                                        .findViewById(R.id.movie_detail_review_item_author_tv);
                                TextView contentTv = reviewItem
                                        .findViewById(R.id.movie_detail_review_item_content_tv);

                                authorTv.setText(author);
                                contentTv.setText(review.getContent());

                                reviewsContainer.addView(reviewItem);

                                displayedCount++;

                                if (!reviewIterator.hasNext() || displayedCount == MAX_REVIEWS_COUNT) {
                                    // Setup last review & exit loop
                                    View separator = reviewItem
                                            .findViewById(R.id.movie_detail_review_item_separator_v);
                                    separator.setVisibility(View.INVISIBLE);

                                    break;
                                }
                            }

                        } else {
                            // Hide reviews view
                            hideReviewsView();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Stop loading
                        reviewsLoading.setVisibility(View.GONE);

                        hideReviewsView();

                        Toast.makeText(MovieDetailsActivity.this,
                                getString(R.string.movie_detail_load_reviews_error),
                                Toast.LENGTH_LONG).show();

                        Log.e(TAG, "VolleyError: " + error.getMessage());
                    }
                });

        NetworkUtils.get(MovieDetailsActivity.this).addToRequestQueue(reviewsRequest);
    }

    private void hideTrailersView() {
        trailersSeparator.setVisibility(View.GONE);
        trailersLabel.setVisibility(View.GONE);
        trailersContainer.setVisibility(View.GONE);
    }

    private void hideReviewsView() {
        reviewsSeparator.setVisibility(View.GONE);
        reviewsLabel.setVisibility(View.GONE);
        reviewsContainer.setVisibility(View.GONE);
    }

    @OnClick(R.id.movie_detail_favorite_btn)
    void favoriteClicked() {

        if (favorite != null) {
            removeFromFavorites(favorite);
        } else {
            Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(MOVIE_EXTRA_PARAM));
            addToFavorites(movie);
        }

        // TODO move to a method retrieves full movie data
//        Cursor cursor = getApplicationContext().getContentResolver()
//                .query(FavoritesProvider.Favorites.withUid(movie.getId()),
//                        new String[]{FavoritesDatabase.FavoriteColumns.UID},
//                        null,
//                        null,
//                        null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            removeFromFavorites(movie);
//        } else {
//            addToFavorites(movie);
//        }

    }

    // TODO Test Me!
    private void addToFavorites(@NonNull final Movie movie) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                favoritesDao.insert(movie);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        favorite = movie;

                        setupFavorite(true);

                        Toast.makeText(MovieDetailsActivity.this,
                                String.format(getString(R.string.movie_detail_favorite_added), movie.getTitle()),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

//        ContentValues cv = new ContentValues();
//        cv.put(FavoritesDatabase.FavoriteColumns.UID, movie.getId());
//        cv.put(FavoritesDatabase.FavoriteColumns.TITLE, movie.getTitle());
//        cv.put(FavoritesDatabase.FavoriteColumns.POSTER_PATH, movie.getPosterPath());
//        cv.put(FavoritesDatabase.FavoriteColumns.OVERVIEW, movie.getOverview());
//        cv.put(FavoritesDatabase.FavoriteColumns.RELEASE_DATE, movie.getReleaseDate());
//        cv.put(FavoritesDatabase.FavoriteColumns.VOTE_AVERAGE, movie.getVoteAverage());
//        Uri newUri = getApplicationContext().getContentResolver()
//                .insert(FavoritesProvider.Favorites.CONTENT_URI, cv);

    }

    // TODO Test Me!
    private void removeFromFavorites(@NonNull final Movie movie) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                favoritesDao.delete(movie);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        favorite = null;

                        setupFavorite(false);

                        Toast.makeText(MovieDetailsActivity.this,
                                String.format(getString(R.string.movie_detail_favorite_removed),
                                        movie.getTitle()),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

//        int count = getApplicationContext().getContentResolver()
//                .delete(FavoritesProvider.Favorites.withUid(movie.getId()),
//                        null, null);

//        if (count > 0) {
//            Toast.makeText(MovieDetailsActivity.this,
//                    String.format(getString(R.string.movie_detail_favorite_removed),
//                            movie.getTitle()),
//                    Toast.LENGTH_LONG).show();
//        }
    }

    private void setupFavorite(boolean isFavorite) {
        // TODO
        if (isFavorite) {
            favoriteButton.setText(getString(R.string.movie_detail_favorite_button_added));
            favoriteButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            favoriteButton.setTextColor(Color.WHITE);
        } else {
            favoriteButton.setText(getString(R.string.movie_detail_favorite_button));
            favoriteButton.setBackgroundColor(Color.LTGRAY);
            favoriteButton.setTextColor(Color.BLACK);
        }
    }
}
