package com.example.android.popularmovies.data;

import java.util.List;

/**
 * Created by john on 03/03/18.
 */

public class Movie {

    //    poster_path         string or null      optional
    //    adult               boolean             optional
    //    overview            string              optional
    //    release_date        string              optional
    //    genre_ids           array[integer]      optional
    // OR
    //    genres              array[object]       optional
    //      id                    integer           optional
    //      name                  string            optional
    //    id                  integer             optional
    //    original_title      string              optional
    //    original_language   string              optional
    //    title               string              optional
    //    backdrop_path       string or null      optional
    //    popularity          number              optional
    //    vote_count          integer             optional
    //    video               boolean             optional
    //    vote_average        number              optional

    private String posterPath;
    private boolean adult;
    private String overview;
    private String release_date;
    private List<Integer> genreIds;
    private List<Genre> genres;
    private Integer id;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private float popularity;
    private Integer voteCount;
    private boolean video;
    private float voteAverage;

    public static class Genre {
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public Movie() {
    }

    // TODO Delete this constructor
    // Poster path constructor for simpler testing
    public Movie(String posterPath) {
        setPosterPath(posterPath);
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }
}
