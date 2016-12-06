package com.xuyonghong.trendingmovies.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.model.Movie;
import com.xuyonghong.trendingmovies.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    /// use ButterKnife to automatically bind fields
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.movie_poster2) ImageView moviePoster;
    @BindView(R.id.debut_time) TextView debutTime;
    @BindView(R.id.movie_rating) TextView movieRating;
    @BindView(R.id.movie_intro) TextView movieIntro;

    private static final String DEBUG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        Intent intent = getActivity().getIntent();
        Movie movieItem = (Movie) intent.getSerializableExtra("MOVIE_ITEM");
        Log.d(DEBUG_TAG, movieItem.toString());

        // set the movie item in the fragment
        movieTitle.setText(movieItem.getTitle());
        MyUtils.populateImageViewWithUrl(getActivity(), moviePoster, movieItem.getPoster_path());
        debutTime.setText(MyUtils.getYearInDateString(movieItem.getRelease_date()));
        movieRating.setText(movieItem.getVote_average());
        movieIntro.setText(movieItem.getOverview());

        return view;
    }

}
