package com.example.android.popularmovies.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

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

    @Query("SELECT * FROM " + TABLE_NAME)
    List<Movie> getAll();

    @Query("SELECT * FROM " + TABLE_NAME + " LIMIT :limit OFFSET :offset")
    List<Movie> getAll(int limit, int offset);

    @Query("SELECT * FROM " + TABLE_NAME + " WHERE " + Columns.UID + " = :movieId")
    Movie get(long movieId);

    @Insert
    void insert(Movie movie);

    @Insert
    void insertAll(Movie... movies);

    @Delete
    void delete(Movie movie);
}