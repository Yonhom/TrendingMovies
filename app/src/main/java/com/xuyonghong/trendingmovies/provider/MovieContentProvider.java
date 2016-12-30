package com.xuyonghong.trendingmovies.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.CONTENT_URI;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.MOVIE_TABLE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable._ID;

public class MovieContentProvider extends ContentProvider {
    //
    private static final int MOVIE_TABLE_ID = 1;
    private static final int MOVIE_TABLE_ITEM_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        /*
        addUri() called here, for all the contentURI patterns that the provider
        should recognize
         */

        // set the integer value 1 for the data in movie table
        sUriMatcher.addURI(MovieContracts.AUTHORITY, MOVIE_TABLE, MOVIE_TABLE_ID);

        // set the integer value 2 for the single row data in movie table
        sUriMatcher.addURI(MovieContracts.AUTHORITY, MOVIE_TABLE + "/#", MOVIE_TABLE_ITEM_ID);
    }

    private MovieDatabaseHelper mMovieHelper;

    private SQLiteDatabase movieDb;

    public MovieContentProvider() {

    }

    @Override
    public boolean onCreate() {
        /*
        create a new database helper, this method returns instantly.
        notice that the database is not created or opened until db.getWritableDatabase
        is called
         */
        mMovieHelper = new MovieDatabaseHelper(getContext());

        /*
        create a writable database which will trigger its creation
        if it doesn't already exist.
         */
        movieDb = mMovieHelper.getWritableDatabase();

        return (movieDb == null) ? false : true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_TABLE_ID:
                count = movieDb.delete(MOVIE_TABLE, selection, selectionArgs);
                break;

            case MOVIE_TABLE_ITEM_ID:
                String id = uri.getPathSegments().get(1);
                count = movieDb.delete(
                        MOVIE_TABLE,
                        _ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE_TABLE_ID:
                return "vnd.android.cursor.dir/vnd.xuyonghong.trendingmovies";

            case MOVIE_TABLE_ITEM_ID:
                return "vnd.android.cursor.item/vnd.xuyonghong.trendingmovies";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // add a new movie item record
        long rowID = movieDb.insert(MovieContracts.MovieTable.MOVIE_TABLE, "", values);

        // if the record is added successfully
        if (rowID >= 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLiteException("Failed to add a record into " + uri);
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MovieContracts.MovieTable.MOVIE_TABLE);

        switch (sUriMatcher.match(uri)) {
            case MOVIE_TABLE_ID:  //if the incoming uri is for all the data in movie table
                break;

            case MOVIE_TABLE_ITEM_ID:  // if the incoming uri is for a single row of data in movie table
                qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
                break;
            default: // if the uri is not recognized, do error handling here
        }

        // do the query below
        Cursor cursor = qb.query(
                movieDb, projection, selection, selectionArgs, null, null, sortOrder);

        // register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_TABLE_ID:
                count = movieDb.update(
                        MOVIE_TABLE, values, selection, selectionArgs);
                break;

            case MOVIE_TABLE_ITEM_ID:
                count = movieDb.update(
                        MOVIE_TABLE,
                        values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numValues = 0;
        movieDb.beginTransaction(); //开始事务
        try {
            //数据库操作
            numValues = values.length;
            for (int i = 0; i < numValues; i++) {
                insert(uri, values[i]);
            }
            movieDb.setTransactionSuccessful(); //别忘了这句 Commit
        } finally {
            movieDb.endTransaction(); //结束事务
        }
        return numValues;
    }
}