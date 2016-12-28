package com.xuyonghong.trendingmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.model.Movie;
import com.xuyonghong.trendingmovies.util.MyUtils;
import com.xuyonghong.trendingmovies.view.GridViewMovieItem;

import java.util.List;

/**
 * adapter for movie art gridview
 * Created by xuyonghong on 2016/12/1.
 */

public class ImageAdapter extends BaseAdapter {
    private List<Movie> data;
    private Context context;

    private LayoutInflater inflater;

    public ImageAdapter(List<Movie> movieArray, Context context) {
        data = movieArray;
        this.context = context;
        inflater = LayoutInflater.from(context);
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
        GridViewMovieItem imageView;
        if (convertView == null) {
            imageView = (GridViewMovieItem) inflater.inflate(
                    R.layout.grid_view_movie_item, null);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (GridViewMovieItem) convertView;
        }

        // populate the imageview with image url
        MyUtils.populateImageViewWithUrl(
                context, imageView, data.get(position).getPoster_path());

        return imageView;
    }
}
