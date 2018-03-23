package com.example.android.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by john on 23/03/18.
 */

public class Video {

    // id          string                                                          optional
    // iso_639_1   string                                                          optional
    // iso_3166_1  string                                                          optional
    // key         string                                                          optional
    // name        string                                                          optional
    // site        string                                                          optional
    // size        integer     Allowed Values: 360, 480, 720, 1080                 optional
    // type        string      Allowed Values: Trailer, Teaser, Clip, Featurette   optional

    private String name;
    // TODO finish me

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Video{" +
                "name='" + name + '\'' +
                '}';
    }

    public static class Page {

        // id               integer         optional
        // results          array[Video]    optional

        private Integer id;
        private List<Video> results;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<Video> getResults() {
            return results;
        }

        public void setResults(List<Video> results) {
            this.results = results;
        }

        @Override
        public String toString() {
            return "Page{" +
                    "id=" + id +
                    ", results=" + results +
                    '}';
        }
    }
}
