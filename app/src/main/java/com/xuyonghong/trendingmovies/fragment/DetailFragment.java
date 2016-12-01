package com.xuyonghong.trendingmovies.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.bean.Movie;

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

        return view;
    }

}
