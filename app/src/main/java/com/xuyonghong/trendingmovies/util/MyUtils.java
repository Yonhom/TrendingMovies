package com.xuyonghong.trendingmovies.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by xuyonghong on 2016/12/2.
 */

public class MyUtils {

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
}
