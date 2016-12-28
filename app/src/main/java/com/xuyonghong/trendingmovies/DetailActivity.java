package com.xuyonghong.trendingmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.xuyonghong.trendingmovies.fragment.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Fragment detailFragment = new DetailFragment();
        Intent intent = getIntent();
        int selectedMovieID = intent.getIntExtra("MOVIE_ITEM_ID", 0);
        Bundle args = new Bundle();
        args.putInt("MOVIE_ITEM_ID", selectedMovieID);
        detailFragment.setArguments(args);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_detail, detailFragment).commit();
        }

    }

}
