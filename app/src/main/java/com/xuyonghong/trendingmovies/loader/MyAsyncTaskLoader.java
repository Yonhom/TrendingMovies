package com.xuyonghong.trendingmovies.loader;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyonghong on 2016/12/5.
 */

public class MyAsyncTaskLoader extends AsyncTaskLoader<List> {
    private static final String DEBUG_TAG = MyAsyncTaskLoader.class.getSimpleName();

    private ConnectivityManager cManager;
    private NetworkInfo activeNetworkInfo;

    private Context context;

    public MyAsyncTaskLoader(Context context) {
        super(context);
        this.context = context;
    }

    // when initLoader of LoaderManager is called, this method will be called first
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List loadInBackground() {
        List<Movie> movieList = new ArrayList<>();
        cManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworkInfo = cManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            // internet connected, fetch data
            String responseStr = getResponseFromReqUrl(getRequestUrl());

            if (responseStr != null) {
                movieList.addAll(movieJsonToArray(responseStr));
            }



        } else {
            // remind the user that there is internet connectivity problem
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            context,
                            "Mayday, mayday! The internet is down, now we are all in peril!!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });

        }

        return movieList;
    }

    /**
     * Call Hierarchy:
     * MyAsyncTaskLoader.deliverResult(List)
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
    public void deliverResult(List data) {
        super.deliverResult(data);
    }

    /**
     * get the movie request url
     * @return
     */
    private String getRequestUrl() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
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
     * get response from url
     * @param urlStr
     * @return
     */
    @Nullable
    private String getResponseFromReqUrl(String urlStr) {
        InputStream is = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try{
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);  // default is true
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Toast.makeText(context,
                        "Response code is" + responseCode + ", not 200",
                        Toast.LENGTH_SHORT).show();
                return null;
            }

            is = conn.getInputStream();
            if (is == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line;

            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                return null;
            }
            String responseStr = buffer.toString();
            Log.d(DEBUG_TAG, responseStr);

            return responseStr;
        }catch(IOException e){
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * parse json response to movie array list
     * @param jsonStr
     * @return
     */
    private List<Movie> movieJsonToArray(String jsonStr) {
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

                movieArray.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(DEBUG_TAG, movieArray.toString());
        return movieArray;
    }



}
