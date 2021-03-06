package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.TMDbUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by john on 10/04/18.
 */

public abstract class AbstractDiscoveryAdapter extends RecyclerView.Adapter<AbstractDiscoveryAdapter.ViewHolder> {

    private final Context context;

    // Registered Movie Click Listeners
    private List<MovieClickListener> movieClickListeners = new ArrayList<MovieClickListener>();

    protected AbstractDiscoveryAdapter(Context context) {
        this.context = context;
    }

    /**
     * ItemType could be used to view some items in a different way such as taking a whole row span
     * for the loading item in a grid view.
     */
    private enum ItemType {
        MOVIE, LOADING_MOVIE
    }


    /**
     * Called when RecyclerView needs a new {@link AbstractDiscoveryAdapter.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(AbstractDiscoveryAdapter.ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(AbstractDiscoveryAdapter.ViewHolder, int)
     */
    @Override
    public AbstractDiscoveryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_discovery, parent, false);

        return new AbstractDiscoveryAdapter.ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link AbstractDiscoveryAdapter.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link AbstractDiscoveryAdapter.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(AbstractDiscoveryAdapter.ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(AbstractDiscoveryAdapter.ViewHolder holder, int position) {

        final Movie movie = getItem(position);

        String posterPath = movie.getPosterPath();


        ItemType itemType = getItemType(position);
        switch (itemType) {
            case MOVIE:

                String title = movie.getTitle();

                if (TextUtils.isEmpty(title)) {
                    title = "UNTITLED";
                }

                if (TextUtils.isEmpty(movie.getPosterPath())) {

                    holder.title.setText(title);
                    holder.image.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.bg_movie_thumb));

                    holder.loading.setVisibility(View.GONE);
                    holder.image.setVisibility(View.VISIBLE);
                    holder.title.setVisibility(View.VISIBLE);

                } else {
                    String posterURL = TMDbUtils.buildPosterURL(posterPath, TMDbUtils.PosterSize.W185);

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
                    holder.title.setVisibility(View.GONE);
                    holder.image.setVisibility(View.VISIBLE);

                    holder.image.setContentDescription(title);
                }

                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (AbstractDiscoveryAdapter.MovieClickListener listener : movieClickListeners) {
                            listener.onMovieClicked(movie);
                        }
                    }
                });

                break;

            case LOADING_MOVIE:
                holder.image.setVisibility(View.GONE);
                holder.title.setVisibility(View.GONE);
                holder.loading.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * ViewHolder to display a discovery movie item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.discovery_item_image_iv)
        ImageView image;

        @BindView(R.id.discovery_item_loading_pb)
        ProgressBar loading;

        @BindView(R.id.discovery_item_title_tv)
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    /**
     * Called by RecyclerView when it starts observing this Adapter.
     * <p>
     * Keep in mind that same adapter may be observed by multiple RecyclerViews.
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter.
     * @see #onDetachedFromRecyclerView(RecyclerView)
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) recyclerView.getLayoutManager())
                    .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        /**
                         * Returns the number of span occupied by the item at <code>position</code>.
                         *
                         * @param position The adapter position of the item
                         * @return The number of spans occupied by the item at the provided position
                         */
                        @Override
                        public int getSpanSize(int position) {
                            ItemType itemType = getItemType(position);
                            switch (itemType) {
                                case MOVIE:
                                    return 1;
                                case LOADING_MOVIE:
                                    return context.getResources()
                                            .getInteger(R.integer.discovery_grid_columns);
                            }
                            return 0;
                        }
                    });
        }
    }

    /**
     * Called by RecyclerView when it stops observing this Adapter.
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     * @see #onAttachedToRecyclerView(RecyclerView)
     */
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
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

    protected abstract Movie getItem(int position);

    public abstract void startLoading();

    public abstract void stopLoading();

    public abstract void clear();
}
