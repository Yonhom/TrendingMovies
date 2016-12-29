package com.xuyonghong.trendingmovies.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xuyonghong.trendingmovies.DetailActivity;
import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.adapter.MovieItemCursorAdapter;
import com.xuyonghong.trendingmovies.loader.MyAsyncTaskLoader;
import com.xuyonghong.trendingmovies.provider.MovieContracts;
import com.xuyonghong.trendingmovies.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * this fragment is for the movie list
 *
 * the main method call order:
 * constructor --> onCreate --> onCreateView --> onActivityCreated --> onCreateLoader --> onLoadFinished
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String DEBUG_TAG = MainFragment.class.getSimpleName();

    private GridView movieGrid; // for displaying movie

    private Date last_update_time = new Date(); // the lastest update time

    private static final int UPDATE_INTERVAL = 30 * 60 * 1000; // 30mins
    
    private String lastRankingOrder = "popular";

    private Loader fetchOnlineMovieListLoader;

    private static final int FETCH_ONLINE_MOVIE_LIST_LOADER = 0;

    private CursorAdapter adapter;

    private Cursor cursor;

    public MainFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
        Log.d(DEBUG_TAG, "MainFragment()");


    }

    /**
     * if the user has pressed Refresh button, update
     * if user had changed the movie ranking preference, update
     * if the update interval is 30 mins or bigger, update
     * else dont update
     * @return
     */
    private boolean updateFragmentOrNot() {
        SharedPreferences dsp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String newRankingOrder =
                dsp.getString(getString(R.string.movie_ranking_type_key),"popular");
        if (!newRankingOrder.equals(lastRankingOrder)) {
            lastRankingOrder = newRankingOrder;
            return true;
        }

        // we only update the data every 30 mins
        Date newUpdateTime = new Date();
        if ((newUpdateTime.getTime() - last_update_time.getTime()) > UPDATE_INTERVAL) {
            last_update_time = newUpdateTime;
            return true;
        }

        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(DEBUG_TAG, "onActivityCreated");

        // init the MyAsyncTaskLoader
        fetchOnlineMovieListLoader = getLoaderManager().initLoader(0, null, this);
    }


    /// loader related methods, those methods will be called as the loader's stage changes
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(DEBUG_TAG, "onCreateLoader");
        // create a loader here, if there isn't a existing one with the id
        return new MyAsyncTaskLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(DEBUG_TAG, "onLoadFinished");
        if (loader.getId() == FETCH_ONLINE_MOVIE_LIST_LOADER) {
            cursor = data;
            adapter.swapCursor(cursor);

            // insert data to database
            List<ContentValues> valueList = new ArrayList<>();
            while (data.moveToNext()) {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(data, values);
                valueList.add(values);
            }
            getContext().getContentResolver().bulkInsert(
                    MovieContracts.MovieTable.CONTENT_URI,
                    valueList.toArray(new ContentValues[valueList.size()]));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
    /// load related methods


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate");

//        updateFragment();

    }

    @Override
    public void onStart() {
        super.onStart();
//        if (updateFragmentOrNot()) {
//            fetchOnlineMovieListLoader.forceLoad();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        movieGrid = (GridView)view.findViewById(R.id.movie_grid);

        adapter = new MovieItemCursorAdapter(
                getContext(), cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        movieGrid.setAdapter(adapter);

        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("MOVIE_ITEM_ID", position);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.findItem(R.id.action_refresh) == null) {
            inflater.inflate(R.menu.menu_fragment_main, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchOnlineMovieListLoader.forceLoad();
                break;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }
        return true;
    }
}
