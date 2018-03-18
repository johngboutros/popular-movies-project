package com.example.android.popularmovies.components;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by john on 11/03/18.
 * <p>
 * Custom OnScrollListener to handle {@link RecyclerView} pagination.
 *
 * @see <a href="http://blog.iamsuleiman.com/android-pagination-tutorial-getting-started-recyclerview ">source</a>
 */

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    LinearLayoutManager layoutManager;

    public PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {

                // FIX: Cannot call this method in a scroll callback. Scroll callbacks mightbe run
                // during a measure & layout pass where you cannot change theRecyclerView data.
                // Any method call that might change the structureof the RecyclerView or the
                // adapter contents should be postponed tothe next frame.
                recyclerView.post(new Runnable() {
                    public void run() {
                        loadMoreItems();
                    }
                });
            }
        }
    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}
