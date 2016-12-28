package com.xuyonghong.trendingmovies.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.DBNAME;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.DB_VERSION;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.MOVIE_TABLE;

/**
 * Created by xuyonghong on 2016/12/26.
 */

public class MovieDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Initialize the open helper for the provider's open data repository
     * do not do the database creation and update here
     * @param context
     */
    public MovieDatabaseHelper(Context context) {
        super(context, DBNAME, null, DB_VERSION);
    }

    /**
     * create the data repository
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieContracts.MovieTable.CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE);
        onCreate(db);
    }
}
