package com.xuyonghong.trendingmovies.loader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.model.Movie;
import com.xuyonghong.trendingmovies.util.MyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.BACKDROP_PATH;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.MOVIE_ID;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.MOVIE_TABLE_PROJECTION;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.OVERVIEW;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.POSTER_PATH;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.RELEASE_DATE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.TITLE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.VOTE_AVERAGE;
import static com.xuyonghong.trendingmovies.util.MyUtils.getResponseFromReqUrl;

/**
 * Created by xuyonghong on 2016/12/5.
 */

public class MovieListAsyncTaskLoader extends AsyncTaskLoader<MatrixCursor> {
    private static final String DEBUG_TAG = MovieListAsyncTaskLoader.class.getSimpleName();

    private ConnectivityManager cManager;
    private NetworkInfo activeNetworkInfo;

    private List<Movie> movieList = new ArrayList<>();

    private String[] columnNames = MOVIE_TABLE_PROJECTION;
    private MatrixCursor mc = new MatrixCursor(columnNames);

    private List<String> collectedUrls;

    private Context context;

    public MovieListAsyncTaskLoader(Context context) {
        super(context);
        this.context = context;
    }

    public MovieListAsyncTaskLoader(Context context, List<String> urls) {
        super(context);
        collectedUrls = urls;
        this.context = context;
    }

    /**
     *     when initLoader of LoaderManager is called, this method will be called first
     *     this method will be called each time the MainFragment is returned from
     *     backstrack
     */
    @Override
    protected void onStartLoading() {
        Log.d(DEBUG_TAG, "onStartLoading()");
        // if the data is already exist, just deliver it to the loader callbacks
        if (movieList.size() > 0) {
            deliverResult(mc);
        } else {
            forceLoad();
        }
    }

    @Override
    public MatrixCursor loadInBackground() {
        Log.d(DEBUG_TAG, "loadInBackground()");
        cManager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = cManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (MyUtils.isCollectedMoviesShowing(getContext())) {
                if (collectedUrls != null && collectedUrls.size() > 0) {
                    for (String colletedUrl : collectedUrls) {
                        String response =
                                MyUtils.getResponseFromReqUrl(getContext(), colletedUrl);
                        Movie movie = movieJsonToMovie(response);
                        movieList.add(movie);
                    }
                }
            } else {
                // internet connected, fetch data
                String responseStr =
                        getResponseFromReqUrl(getContext(), getRequestUrl());

                if (responseStr != null) {
                    movieList.addAll(movieJsonToArray(responseStr));
                }
            }


            return arrayToMatrixCursor(movieList);

        } else {
            // remind the user that there is internet connectivity problem
            ((Activity)getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getContext(),
                            "Mayday, mayday! The internet is down, now we are all in peril!!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
            return arrayToMatrixCursor(movieList);
        }

    }

    private MatrixCursor arrayToMatrixCursor(List<Movie> movieList) {

        for (int i = 0; i < movieList.size(); i++) {
            MatrixCursor.RowBuilder rowBuilder = mc.newRow();
            Movie movie = movieList.get(i);
            rowBuilder.add(columnNames[0], i)
                    .add(columnNames[1], movie.getPoster_path())
                    .add(columnNames[2], movie.getTitle())
                    .add(columnNames[3], movie.getBackdrop_path())
                    .add(columnNames[4], movie.getRelease_date())
                    .add(columnNames[5], movie.getVote_average())
                    .add(columnNames[6], movie.getOverview())
                    .add(columnNames[7], movie.getId());
        }

        return mc;

    }

    /**
     * Call Hierarchy:
     * MovieListAsyncTaskLoader.deliverResult(List)
     * --->
     * Loader.deliverResult(D)
     * --->
     * LoaderManagerImpl.onLoadComplete(Loader<Object>, Object)
     * --->
     * LoaderManagerImpl.callOnLoadFinished(Loader<Object>, Object)
     * --->
     * MainFragment.onLoadFinished(Loader<List>, List)
     */
    @Override
    public void deliverResult(MatrixCursor data) {
        Log.d(DEBUG_TAG, "deliverResult()");
        super.deliverResult(data);
    }

    /**
     * get the movie request url
     * @return
     */
    private String getRequestUrl() {

        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(getContext());

        String lastRankingOrder =
                sp.getString(context.getString(R.string.movie_ranking_type_key), "popular");

        String baseUrlStr = "https://api.themoviedb.org/3/movie/" + lastRankingOrder + "?";

        Uri uri = Uri.parse(baseUrlStr).buildUpon()
                .appendQueryParameter("language", "zh")
                .appendQueryParameter("api_key", "abc9d273fe2afffe7d8b56710a96ae15")
                .build();
        String requestUrl = uri.toString();
        return requestUrl;
    }

    /**
     * parse json response to movie array list
     * @param jsonStr
     * @return
     */
    public List<Movie> movieJsonToArray(String jsonStr) {
        ArrayList<Movie> movieArray = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray results = jsonObj.getJSONArray("results");
            for(int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.getJSONObject(i); // the individual json object

                Movie movie = new Movie();
                movie.setPoster_path(jsonObject.getString("poster_path"));
                movie.setTitle(jsonObject.getString("title"));
                movie.setBackdrop_path(jsonObject.getString("backdrop_path"));
                movie.setRelease_date(jsonObject.getString("release_date"));
                movie.setVote_average(jsonObject.getString("vote_average"));
                movie.setOverview(jsonObject.getString("overview"));
                movie.setId(jsonObject.getString("id"));

                movieArray.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(DEBUG_TAG, movieArray.toString());
        return movieArray;
    }

    public Movie movieJsonToMovie(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            Movie movie = new Movie();
            movie.setBackdrop_path(jsonObj.getString(BACKDROP_PATH));
            movie.setTitle(jsonObj.getString(TITLE));
            movie.setPoster_path(jsonObj.getString(POSTER_PATH));
            movie.setRelease_date(jsonObj.getString(RELEASE_DATE));
            movie.setVote_average(jsonObj.getString(VOTE_AVERAGE));
            movie.setOverview(jsonObj.getString(OVERVIEW));
            movie.setId(String.valueOf(jsonObj.getInt(MOVIE_ID)));

            return movie;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
