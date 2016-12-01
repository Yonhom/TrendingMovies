package com.xuyonghong.trendingmovies.bean;

import java.io.Serializable;

/**
 * Created by xuyonghong on 2016/12/1.
 */

public class Movie implements Serializable {
    private static final String BASE_URL = "https://image.tmdb.org/t/p/w185";

    private String poster_path;

    private String title;
    private String backdrop_path;
    private String release_date;
    private String vote_average;
    private String overview;

    public String getPoster_path() {
        return BASE_URL + poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop_path() {
        return BASE_URL + backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
