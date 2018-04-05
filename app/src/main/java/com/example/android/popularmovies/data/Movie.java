package com.example.android.popularmovies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by john on 03/03/18.
 */

@Entity
@Parcel
public class Movie  {

    //  poster_path         string or null      optional
    //  adult               boolean             optional
    //  overview            string              optional
    //  release_date        string              optional
    //  genre_ids           array[integer]      optional
    //  | OR
    //  genres              array[object]       optional
    //      id                    integer           optional
    //      name                  string            optional
    //  id                  integer             optional
    //  original_title      string              optional
    //  original_language   string              optional
    //  title               string              optional
    //  backdrop_path       string or null      optional
    //  popularity          number              optional
    //  vote_count          integer             optional
    //  video               boolean             optional
    //  vote_average        number              optional

    public interface Columns {
        String UID = "id";
        String TITLE = "title";
        String POSTER_PATH = "poster_path";
        String OVERVIEW = "overview";
        String RELEASE_DATE = "release_date";
        String VOTE_AVERAGE = "voteAverage";
        String CREATION_DATE = "creationDate";
    }

    @ColumnInfo(name = Columns.POSTER_PATH)
    @SerializedName("poster_path")
    String posterPath;

    boolean adult;

    @ColumnInfo(name = Columns.OVERVIEW)
    String overview;

    @ColumnInfo(name = Columns.RELEASE_DATE)
    @SerializedName("release_date")
    String releaseDate;

    @SerializedName("genre_ids")
    List<Integer> genreIds;

    List<Genre> genres;

    @PrimaryKey
    Integer id;

    @SerializedName("original_title")
    String originalTitle;

    @SerializedName("original_language")
    String originalLanguage;

    @ColumnInfo(name = Columns.TITLE)
    String title;

    @SerializedName("backdrop_path")
    String backdropPath;

    float popularity;

    @SerializedName("vote_count")
    Integer voteCount;

    boolean video;

    @ColumnInfo(name = Columns.VOTE_AVERAGE)
    @SerializedName("vote_average")
    float voteAverage;

    @Parcel
    public static class Genre {
        Integer id;
        String name;

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

        @Override
        public String toString() {
            return "Genre{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Page {

        // page            integer         optional
        // results         array[Movie]    optional
        // total_results   integer         optional
        // total_pages     integer         optional

        private Integer page;
        private List<Movie> results;
        @SerializedName("total_results")
        private Integer totalResults;
        @SerializedName("total_pages")
        private Integer totalPages;

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public List<Movie> getResults() {
            return results;
        }

        public void setResults(List<Movie> results) {
            this.results = results;
        }

        public Integer getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        @Override
        public String toString() {
            return "Page{" +
                    "page=" + page +
                    ", results=" + results +
                    ", totalResults=" + totalResults +
                    ", totalPages=" + totalPages +
                    '}';
        }
    }


    public Movie() {
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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

    @Override
    public String toString() {
        return "Movie{" +
                "posterPath='" + posterPath + '\'' +
                ", adult=" + adult +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", genreIds=" + genreIds +
                ", genres=" + genres +
                ", id=" + id +
                ", originalTitle='" + originalTitle + '\'' +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", title='" + title + '\'' +
                ", backdropPath='" + backdropPath + '\'' +
                ", popularity=" + popularity +
                ", voteCount=" + voteCount +
                ", video=" + video +
                ", voteAverage=" + voteAverage +
                '}';
    }
}
