package com.xuyonghong.trendingmovies.provider;

import android.net.Uri;

/**
 * Created by xuyonghong on 2016/12/26.
 */

public class MovieContracts {
    public static final String DBNAME = "movie_base";
    public static final int DB_VERSION = 1;
    public static final String AUTHORITY = "com.xuyonghong.trendingmovies.provider";

    public static class MovieTable {
        public static final String MOVIE_TABLE = "movie_table";
        public static final String URL = "content://" + AUTHORITY + "/" + MOVIE_TABLE;
        public static final Uri CONTENT_URI = Uri.parse(URL);

        // the movie table column info
        public static final String _ID = "_ID";
        public static final String POSTER_PATH = "poster_path";
        public static final String TITLE = "title";
        public static final String BACKDROP_PATH = "backdrop_path";
        public static final String RELEASE_DATE = "release_date";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String OVERVIEW = "overview";

        public static final String CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MOVIE_TABLE +
                        " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        POSTER_PATH + " TEXT NOT NULL, " +
                        TITLE + " TEXT NOT NULL, " +
                        BACKDROP_PATH + " TEXT NOT NULL, " +
                        RELEASE_DATE + " TEXT NOT NULL, " +
                        VOTE_AVERAGE + " TEXT NOT NULL, " +
                        OVERVIEW + " TEXT NOT NULL" +
                        ")";

        public static final String[] MOVIE_TABLE_PROJECTION = new String[] {
            _ID, POSTER_PATH, TITLE, BACKDROP_PATH, RELEASE_DATE, VOTE_AVERAGE, OVERVIEW
        };
    }



}
