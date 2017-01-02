package com.xuyonghong.trendingmovies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_MOVIE_ID;

/**
 * Created by xuyonghong on 2016/12/2.
 */

public class MyUtils {

    public static final String DEBUG_TAG = MyUtils.class.getSimpleName();

    /**
     * construct a request url
     * @param baseUrl
     * @param keys
     * @param values
     * @return
     * @throws MalformedURLException
     */
    public static URL getRequestUrl(String baseUrl, String[] keys, String[] values) throws MalformedURLException {
        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        for (int i = 0; i < keys.length; i++) {
            builder.appendQueryParameter(keys[i], values[i]);
        }

        Uri uri = builder.build();

        return new URL(uri.toString());
    }

    /**
     * get response from url
     * @param urlStr
     * @return
     */
    @Nullable
    public static String getResponseFromReqUrl(Context context, String urlStr) {
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
     * ImageView population util method
     * @param context
     * @param imageView
     * @param imageUrl
     */
    public static void populateImageViewWithUrl(Context context, ImageView imageView, String imageUrl) {

        Picasso.with(context).load(imageUrl).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                // image population failed, use placeholder
            }
        });

    }

    /**
     * turning date string like: yyyy-MM-dd to yyyy string
     * @param dateStr
     * @return
     */
    public static String getYearInDateString(String dateStr) { // dateStr: 2016-12-3
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf = new SimpleDateFormat("yyyy");
        return sdf.format(myDate);

    }

    public static final String APP_START_STATUS_PREFERENCE = "APP_START_STATUS";
    public static final String APP_STARTED_FOR_FIRST_TIME_KEY = "APP_STARTED_FOR_FIRST_TIME";

    public static boolean isAppStartForTheFirstTime(Context context) {
        SharedPreferences app_start_status =
                context.getSharedPreferences(
                        APP_START_STATUS_PREFERENCE, Context.MODE_PRIVATE);
        return app_start_status.getBoolean(APP_STARTED_FOR_FIRST_TIME_KEY, true);
    }


    /**
     * this prefeerence is for store collected movie id, with which we can retrive
     * that movie's info
     */
    public static final String MOVIE_COLLECT_PREF = "movie_collect_pref";
    public static final String MOVIE_COLLECT_KEY = "movie_collect_key";

    public static boolean isMovieCollected(Context context, String movieId) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        MOVIE_COLLECT_PREF, Context.MODE_PRIVATE);
        Set<String> collectSet =
                sharedPreferences.getStringSet(MOVIE_COLLECT_KEY, null);
        if (collectSet != null && collectSet.size() > 0) {
            if (collectSet.contains(movieId)) {
                return true;
            }
        }
        return false;
    }

    public static void collectMovie(Context context, String movieId) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        MOVIE_COLLECT_PREF, Context.MODE_PRIVATE);
        Set<String> stringSet = sharedPreferences.getStringSet(MOVIE_COLLECT_KEY, null);
        if (stringSet == null) {
            stringSet = new HashSet<>();
        }
        stringSet.add(movieId);
        sharedPreferences.edit()
                .putStringSet(MOVIE_COLLECT_KEY, stringSet).apply();
    }

    public static void unCollectMovie(Context context, String movieId) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        MOVIE_COLLECT_PREF, Context.MODE_PRIVATE);
        Set<String> collectSet =
                sharedPreferences.getStringSet(MOVIE_COLLECT_KEY, null);
        collectSet.remove(movieId);
        sharedPreferences.edit()
                .putStringSet(MOVIE_COLLECT_KEY, collectSet).apply();
    }

    public static Set<String> getCollectedMovieIds(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        MOVIE_COLLECT_PREF, Context.MODE_PRIVATE);
        Set<String> collectSet =
                sharedPreferences.getStringSet(MOVIE_COLLECT_KEY, null);
        return collectSet;
    }

    public static final String SHOW_COLLECTED_KEY = "show_collected";

    public static boolean isCollectedMoviesShowing(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        MOVIE_COLLECT_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SHOW_COLLECTED_KEY, false);
    }

    public static void setShowAll(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        MOVIE_COLLECT_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SHOW_COLLECTED_KEY, false).apply();
    }

    public static void setShowCollected(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        MOVIE_COLLECT_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SHOW_COLLECTED_KEY, true).apply();
    }

    public static List<String> cursorToMovieIdArray(Cursor cursor) {
        List<String> strs = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String movieId = cursor.getString(I_MOVIE_ID);
            strs.add(movieId);
        }
        return strs;
    }

}
