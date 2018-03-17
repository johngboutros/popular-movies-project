package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.android.popularmovies.components.PaginationScrollListener;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.GsonRequest;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by john on 03/03/18.
 */

public class DiscoveryAdapter extends RecyclerView.Adapter<DiscoveryAdapter.ViewHolder> {

    private static final String TAG = DiscoveryAdapter.class.getSimpleName();
    private final Context context;

    // Discovered movies list
    private List<Movie> movies = new ArrayList<Movie>();

    // Discovered pages count
    private int pageCount;

    // Total result pages count
    private int totalPageCount;

    // Loading flag
    private boolean isLoading;

    // Current SortOption
    private SortOption currentSortOption = SortOption.POPULARITY;

    // Registered Movie Click Listeners
    private List<MovieClickListener> movieClickListeners = new ArrayList<MovieClickListener>();

    /**
     * ItemType could be used to view some items in a different way such as taking a whole row span
     * for the loading item in a grid view.
     */
    private enum ItemType {
        MOVIE, LOADING_MOVIE
    }

    /**
     * Available sort options provided by the adapter
     */
    public enum SortOption {
        POPULARITY, TOP_RATED, RELEASE_DATE, REVENUE
    }

    /**
     * Initializes the adapter with a {@link Context} and discovery movies data
     *
     * @param context typically the container activity
     */
    public DiscoveryAdapter(Context context) {
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public DiscoveryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discovery, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(DiscoveryAdapter.ViewHolder holder, int position) {

        final Movie movie = movies.get(position);

        String posterPath = movie.getPosterPath();

        String posterURL = TMDbUtils.buildPosterURL(posterPath, TMDbUtils.PosterSize.W185);

        ItemType itemType = getItemType(position);
        switch (itemType) {
            case MOVIE:
                // FIXME BUG: Sometimes app crashes on orientation change (finalized Assets Manager?)
                Picasso.with(context).load(posterURL)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.bg_movie_thumb)
                        .into(holder.image);

                // DEBUG: using Picasso.Listener to detect load failure
                //
                //        Picasso.Builder builder = new Picasso.Builder(context);
                //        builder.listener(new Picasso.Listener() {
                //            @Override
                //            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                //                exception.printStackTrace();
                //            }
                //        });
                //
                //        builder.build().load(movies.get(position).getPosterPath())
                //                .fit()
                //                .centerCrop()
                //                .placeholder(R.drawable.bg_movie_thumb)
                //                .into(holder.image);

                holder.loading.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);

                holder.image.setContentDescription(movie.getTitle());

                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (MovieClickListener listener : movieClickListeners) {
                            listener.onMovieClicked(movie);
                        }
                    }
                });

                break;
            case LOADING_MOVIE:
                holder.image.setVisibility(View.GONE);
                holder.loading.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * ViewHolder to display a discovery movie item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.discovery_item_image_iv)
        ImageView image;

        @BindView(R.id.discovery_item_loading_pb)
        ProgressBar loading;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Adds a movie to the adapter and notifies any registered observers that the item reflected at
     * position has been newly inserted. The item previously at position is now at position
     * position + 1.
     *
     * @param movie
     */
    public void add(Movie movie) {
        movies.add(movie);
        notifyItemInserted(movies.size() - 1);
    }

    /**
     * Adds movies to the adapter.
     *
     * @param movies
     */
    public void addAll(List<Movie> movies) {
        for (Movie movie : movies) {
            add(movie);
        }
    }

    public void remove(Movie city) {
        int position = movies.indexOf(city);
        if (position > -1) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoading = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void startLoading() {
        isLoading = true;

        // An empty movie should be viewed as a loading view
        add(new Movie());
    }

    public void stopLoading() {
        isLoading = false;

        int position = movies.size() - 1;
        Movie item = getItem(position);

        if (item != null) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Movie getItem(int position) {
        return movies.get(position);
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
            if (isLoading) stopLoading();
            clear();
        }

        Integer page = pageCount > 0 ? pageCount + 1 : null;

        String url = null;

        switch (this.currentSortOption) {
            case POPULARITY:
                url = TMDbUtils.buildPopularMoviesURL().toString();
                break;
            case TOP_RATED:
                url = TMDbUtils.buildTopRatedMoviesURL().toString();
                break;
            case RELEASE_DATE:
                url = TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.RELEASE_DATE, page).toString();
                break;
            case REVENUE:
                url = TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.REVENUE, page).toString();
                break;
            default:
                url = TMDbUtils.buildDiscoveryUrl(TMDbUtils.SortBy.POPULARITY, page).toString();
        }

        startLoading();

        Request movieRequest
                = new GsonRequest<Movie.Page>(Request.Method.GET,
                url,
                null,
                Movie.Page.class,
                null,
                new Response.Listener<Movie.Page>() {
                    @Override
                    public void onResponse(Movie.Page moviePage) {
                        stopLoading();
                        Log.d(TAG, "Movie Page: " + moviePage);

                        pageCount = moviePage.getPage();
                        totalPageCount = moviePage.getTotalPages();
                        addAll(moviePage.getResults());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopLoading();
                        Toast.makeText(context,
                                "Couldn't load movies", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "VolleyError: " + error.getMessage());
                    }
                });

        NetworkUtils.get(context).addToRequestQueue(movieRequest);
    }

    /**
     * Custom Pagination {@link OnScrollListener} instance to be set to the {@link RecyclerView}.
     */
    public static class ScrollListener extends PaginationScrollListener {

        private final DiscoveryAdapter adapter;

        /**
         * Initializes a new ScrollListener with an Adapter and a LayoutManager
         *
         * @param adapter {@link DiscoveryAdapter}
         * @param layoutManager {@link RecyclerView}'s LayoutManager
         */
        public ScrollListener(final DiscoveryAdapter adapter, LinearLayoutManager layoutManager) {
            super(layoutManager);
            this.adapter = adapter;

            if (layoutManager instanceof GridLayoutManager) {
                ((GridLayoutManager) layoutManager)
                        .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            /**
                             * Returns the number of span occupied by the item at <code>position</code>.
                             *
                             * @param position The adapter position of the item
                             * @return The number of spans occupied by the item at the provided position
                             */
                            @Override
                            public int getSpanSize(int position) {
                                ItemType itemType = adapter.getItemType(position);
                                switch (itemType) {
                                    case MOVIE:
                                        return 1;
                                    case LOADING_MOVIE:
                                        return adapter.context.getResources()
                                                .getInteger(R.integer.discovery_grid_columns);
                                }
                                return 0;
                            }
                        });
            }
        }

        @Override
        protected void loadMoreItems() {
            adapter.discoverMore();
        }

        @Override
        public int getTotalPageCount() {
            return adapter.totalPageCount;
        }

        @Override
        public boolean isLastPage() {
            return adapter.pageCount >= getTotalPageCount();
        }

        @Override
        public boolean isLoading() {
            return adapter.isLoading;
        }
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     * <p>
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {

        Movie item = getItem(position);

        if (item != null) {
            boolean noId = item.getId() == null;
            if (noId) {
                return ItemType.LOADING_MOVIE.ordinal();
            } else {
                return ItemType.MOVIE.ordinal();
            }
        }

        return super.getItemViewType(position);
    }

    private ItemType getItemType(int position) {
        int itemViewType = getItemViewType(position);
        return ItemType.values()[itemViewType];
    }

    /**
     * Movie Click Listener.
     */
    public static interface MovieClickListener {
        /**
         * To be implemented for desired behavior when a movie clicked.
         *
         * @param movie clicked movie
         */
        public void onMovieClicked(Movie movie);
    }

    /**
     * Registers a MovieClickListener
     *
     * @param listener
     */
    public void addMovieClickListener(MovieClickListener listener) {
        movieClickListeners.add(listener);
    }

    /**
     * Unregisters a MovieClickListener
     *
     * @param listener
     */
    public void removeMovieClickListener(MovieClickListener listener) {
        movieClickListeners.remove(listener);
    }


}
