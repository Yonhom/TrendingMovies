package com.xuyonghong.trendingmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.xuyonghong.trendingmovies.model.Movie;
import com.xuyonghong.trendingmovies.util.MyUtils;

import java.util.List;

/**
 * adapter for movie art gridview
 * Created by xuyonghong on 2016/12/1.
 */

public class ImageAdapter extends BaseAdapter {
    private List<Movie> data;
    private Context context;

    public ImageAdapter(List<Movie> movieArray, Context context) {
        data = movieArray;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return a imageview for the poster
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);

            int screenRotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
            if (screenRotation == Surface.ROTATION_0 || screenRotation == Surface.ROTATION_180) {
                  imageView.setLayoutParams(
            new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600));
            } else {
                imageView.setLayoutParams(
                        new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 900));
            }

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // populate the imageview with image url
        MyUtils.populateImageViewWithUrl(context, imageView, data.get(position).getPoster_path());

        return imageView;
    }
}
