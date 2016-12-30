package com.xuyonghong.trendingmovies.fragment;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.CONTENT_URI;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.MOVIE_TABLE_PROJECTION;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /// use ButterKnife to automatically bind fields
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.movie_poster2) ImageView moviePoster;
    @BindView(R.id.debut_time) TextView debutTime;
    @BindView(R.id.movie_rating) TextView movieRating;
    @BindView(R.id.movie_intro) TextView movieIntro;

    private static final String DEBUG_TAG = DetailFragment.class.getSimpleName();

    public static final String DETAIL_FRAGMENT_TAG = DetailFragment.class.getSimpleName();

    private Loader loader;

    private static final int MOVIE_DETAIL_LOADER = 1;

    private int movieItemId;

    private View detailView;

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_POSTER_PATH = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_BACKDROP_PATH = 3;
    public static final int COL_RELEASE_DATE = 4;
    public static final int COL_VOTE_AVERAGE = 5;
    public static final int COL_OVERVIEW = 6;

    public DetailFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // get the selected movie id
            movieItemId = getArguments().getInt("MOVIE_ITEM_ID", 0);
            // init the detail info retriving loader
            loader = getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        } else {
            loader = getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        }



        // Inflate the layout for this fragment
        detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, detailView);

        return detailView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = CONTENT_URI.buildUpon().
                appendPath(String.valueOf(movieItemId )).build();
        return new CursorLoader(
                getContext(), baseUri, MOVIE_TABLE_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToNext()) {
            movieTitle.setText(cursor.getString(COL_TITLE));
            MyUtils.populateImageViewWithUrl(
                    getActivity(),
                    (ImageView) detailView.findViewById(R.id.movie_poster2),
                    cursor.getString(COL_POSTER_PATH));
            debutTime.setText(cursor.getString(COL_RELEASE_DATE));
            movieRating.setText(cursor.getString(COL_VOTE_AVERAGE));
            movieIntro.setText(cursor.getString(COL_OVERVIEW));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setMovieItemId(int movieItemId) {
        this.movieItemId = movieItemId;
    }
}
