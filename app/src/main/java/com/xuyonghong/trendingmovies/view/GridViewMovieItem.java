package com.xuyonghong.trendingmovies.view;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by xuyonghong on 2016/12/6.
 */

public class GridViewMovieItem extends ImageView {

    public GridViewMovieItem(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // set the iamgeview's aspect ratio manually ratio: 3 : 2
        // the drawable inside the iamgeview's ratio may be different
        int refactoredWidth = widthMeasureSpec;
        int refactoredHeight = widthMeasureSpec * 3 / 2;
        setMeasuredDimension(refactoredWidth, refactoredHeight);
    }
}
