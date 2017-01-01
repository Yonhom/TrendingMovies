package com.xuyonghong.trendingmovies.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.util.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.CONTENT_URI;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_MOVIE_ID;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_OVERVIEW;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_POSTER_PATH;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_RELEASE_DATE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_TITLE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.I_VOTE_AVERAGE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.MOVIE_ID;
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

    private static final int MOVIE_DETAIL_LOADER = 1;

    private View detailView;

    /**
     * the unique id string for identifying a movie
     */
    private String movieId;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // get the selected movie id
            movieId = getArguments().getString("MOVIE_ITEM_ID", "0");
            // init the detail info retriving loader
            getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        } else {
            getLoaderManager().restartLoader(MOVIE_DETAIL_LOADER, null, this);
        }

        // Inflate the layout for this fragment
        detailView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, detailView);

        return detailView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Uri baseUri = CONTENT_URI.buildUpon().
//                appendPath(String.valueOf(movieItemId)).build();
        String selection = MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{movieId};
        return new CursorLoader(
                getContext(),
                CONTENT_URI, MOVIE_TABLE_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToNext()) {
            movieTitle.setText(cursor.getString(I_TITLE));
            MyUtils.populateImageViewWithUrl(
                    getActivity(),
                    (ImageView) detailView.findViewById(R.id.movie_poster2),
                    cursor.getString(I_POSTER_PATH));
            debutTime.setText(cursor.getString(I_RELEASE_DATE));
            movieRating.setText(cursor.getString(I_VOTE_AVERAGE));
            movieIntro.setText(cursor.getString(I_OVERVIEW));
            movieId = cursor.getString(I_MOVIE_ID);

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setMovieItemId(String movieItemId) {
        this.movieId = movieItemId;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.findItem(R.id.movie_collect) == null) {
            inflater.inflate(R.menu.menu_fragment_detail, menu);
        }

        if (MyUtils.isMovieCollected(getContext(), movieId)) {
            menu.findItem(R.id.movie_collect).setIcon(R.mipmap.ic_favorite_black_24dp);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.movie_collect:
                if (MyUtils.isMovieCollected(getContext(), movieId)) {
                    MyUtils.unCollectMovie(getContext(), movieId);
                    item.setIcon(R.mipmap.ic_favorite_white_24dp);
                } else {
                    MyUtils.collectMovie(getContext(), movieId);
                    item.setIcon(R.mipmap.ic_favorite_black_24dp);
                }

                break;

            case android.R.id.home:
                getActivity().onBackPressed();
                break;

        }

        return true;
    }


}
