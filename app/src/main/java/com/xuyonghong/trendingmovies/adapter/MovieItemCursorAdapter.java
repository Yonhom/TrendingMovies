package com.xuyonghong.trendingmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.util.MyUtils;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_POSTER_PATH;

/**
 * Created by xuyonghong on 2016/12/29.
 */

public class MovieItemCursorAdapter extends CursorAdapter {


    public MovieItemCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        /*
        NOTE: the inflate(layout, parent)'s parent parameter should be null,
        otherwise the UnsupportedOperationException will be thrown.
        AdapterView is not supposed to be added with child views, it should be
        populated with adapter.
         */
        View view = LayoutInflater.from(context)
                .inflate(R.layout.grid_view_movie_item, null);
        ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String imageUrlStr = cursor.getString(I_POSTER_PATH);
        MyUtils.populateImageViewWithUrl(
                context, (ImageView) view, imageUrlStr);
    }
}
