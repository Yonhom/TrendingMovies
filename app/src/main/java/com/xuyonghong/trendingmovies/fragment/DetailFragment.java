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
import com.xuyonghong.trendingmovies.bean.Movie;
import com.xuyonghong.trendingmovies.util.MyUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private static final String DEBUG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        Movie movieItem = (Movie) intent.getSerializableExtra("MOVIE_ITEM");
        Log.d(DEBUG_TAG, movieItem.toString());

        // set the movie item in the fragment
        TextView movieTitle = (TextView) view.findViewById(R.id.movie_title);
        ImageView movie_poster = (ImageView) view.findViewById(R.id.movie_poster2);
        TextView debutTime = (TextView) view.findViewById(R.id.debut_time);
        TextView movieRating = (TextView) view.findViewById(R.id.movie_rating);
        TextView movieIntro = (TextView) view.findViewById(R.id.movie_intro);

        movieTitle.setText(movieItem.getTitle());
        MyUtils.populateImageViewWithUrl(getActivity(), movie_poster, movieItem.getPoster_path());
        debutTime.setText(MyUtils.getYearInDateString(movieItem.getRelease_date()));
        movieRating.setText(movieItem.getVote_average());
        movieIntro.setText(movieItem.getOverview());

        return view;
    }

}
