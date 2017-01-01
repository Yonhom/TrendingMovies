package com.xuyonghong.trendingmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xuyonghong.trendingmovies.fragment.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DetailFragment detailFragment = new DetailFragment();
        Intent intent = getIntent();
        String selectedMovieID = intent.getStringExtra("MOVIE_ITEM_ID");
        Bundle args = new Bundle();
        args.putString("MOVIE_ITEM_ID", selectedMovieID);
        detailFragment.setArguments(args);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_detail, detailFragment, DetailFragment.DETAIL_FRAGMENT_TAG).commit();
        } else { // in this block, we sure that the activity's fragment is already existed, we get it and give it the movie id
            DetailFragment dFregment =
                    (DetailFragment) getSupportFragmentManager()
                            .findFragmentByTag(DetailFragment.DETAIL_FRAGMENT_TAG);
            dFregment.setMovieItemId(selectedMovieID);
        }

    }

}
