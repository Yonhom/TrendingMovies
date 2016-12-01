package com.xuyonghong.trendingmovies.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xuyonghong.trendingmovies.bean.Movie;

import java.util.List;

/**
 * adapter for movie art gridview
 * Created by xuyonghong on 2016/12/1.
 */

public class ImageAdapter extends BaseAdapter {
    private List<Movie> data = null;
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
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        Picasso.with(context)
                .load(data.get(position).getPoster_path())
                .into(imageView, new Callback() {
            @Override
            public void onSuccess() {
//                Toast.makeText(context, "Image Loadingng Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(context, "Image Loading Fail", Toast.LENGTH_SHORT).show();
            }
        });
        return imageView;
    }
}
