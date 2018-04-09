package com.example.android.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by john on 05/04/18.
 */
@Dao
public interface FavoritesDao {

    String TABLE_NAME = "favorites";

    interface Columns {
        String UID = "uid";
        String TITLE = "title";
        String POSTER_PATH = "poster_path";
        String OVERVIEW = "overview";
        String RELEASE_DATE = "release_date";
        String VOTE_AVERAGE = "vote_average";
        String CREATION_DATE = "creation_date";
    }

    @Query("SELECT * FROM " + TABLE_NAME + " ORDER BY " + Columns.CREATION_DATE + " DESC")
    List<Movie> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " ORDER BY " + Columns.CREATION_DATE + " DESC")
    LiveData<List<Movie>> getAllAsync();

    @Query("SELECT * FROM " + TABLE_NAME + " ORDER BY " + Columns.CREATION_DATE + " DESC")
    Cursor getAllCursor();

    @Query("SELECT * FROM " + TABLE_NAME + " ORDER BY " + Columns.CREATION_DATE + " DESC"
            + " LIMIT :limit OFFSET :offset")
    List<Movie> getAll(int limit, int offset);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + Columns.UID + " = :movieId")
    Movie getMovie(long movieId);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + Columns.UID + " = :movieId")
    Cursor getMovieCursor(long movieId);

    @Insert(onConflict = REPLACE)
    void insert(Movie movie);

    @Insert(onConflict = REPLACE)
    void insertAll(Movie... movies);

    @Delete
    void delete(Movie movie);
}