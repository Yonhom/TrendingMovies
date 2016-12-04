package com.xuyonghong.trendingmovies.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xuyonghong on 2016/12/2.
 */

public class MyUtils {

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
}
