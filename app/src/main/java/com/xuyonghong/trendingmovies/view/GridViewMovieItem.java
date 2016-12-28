package com.xuyonghong.trendingmovies.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by xuyonghong on 2016/12/6.
 */

public class GridViewMovieItem extends ImageView {

    public GridViewMovieItem(Context context) {
        super(context);
    }

    public GridViewMovieItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /// the onMeasure method is called by view's measure(width, height) method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // set the iamgeview's aspect ratio manually ratio: 3 : 2
        // the drawable inside the iamgeview's ratio may be different
        int refactoredWidth = widthMeasureSpec;
        int refactoredHeight = widthMeasureSpec * 3 / 2;
        // this method is called to set the refactored measurement back to the view
        setMeasuredDimension(refactoredWidth, refactoredHeight);
    }
}
