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
import android.widget.Toast;

import com.xuyonghong.trendingmovies.DetailActivity;
import com.xuyonghong.trendingmovies.R;
import com.xuyonghong.trendingmovies.adapter.MovieItemCursorAdapter;
import com.xuyonghong.trendingmovies.loader.MovieListAsyncTaskLoader;
import com.xuyonghong.trendingmovies.provider.MovieContracts;
import com.xuyonghong.trendingmovies.settings.SettingsActivity;
import com.xuyonghong.trendingmovies.util.MyUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    private CursorAdapter adapter;

    private Cursor cursor;

    /**
     * type of movies showing in the grid: all or collected
     * false: show all
     * true: show collected only
     */
    private boolean showCollected = false;

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

        if (MyUtils.isAppStartForTheFirstTime(getContext())) {
            // init the MovieListAsyncTaskLoader
            getLoaderManager().initLoader(0, null, this);
        } else {
            if (MyUtils.isCollectedMoviesShowing(getContext())) {
                reloadData(); // reload collected data from internet
            } else {
                restoreDataFromDatabase();
            }
        }

    }

    /**
     * every time the 'refresh' option item is clicked, call this method to
     * retrive data from internet and display it on UI and store it in the
     * database
     */
    public void reloadData() {
        /*
        cause this method refreshing,  it is garanteed that a previous loader with the
        same id is already exist, so we need to call the restartLoader() method, for
        reloading new data instead of returning back the old data
         */
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * if the app is already installed and started at least once,
     * read data from database unless the user pressed the 'refresh' option item
     */
    public void restoreDataFromDatabase() {
        cursor = getContext().getContentResolver()
                .query(MovieContracts.MovieTable.CONTENT_URI, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) { // if the cursor is null or empty
            Toast.makeText(
                    getContext(),
                    "No data in local database, Please refresh!",
                    Toast.LENGTH_SHORT).show();
        } else {

            adapter.swapCursor(cursor);
        }
    }


    /// loader related methods, those methods will be called as the loader's stage changes
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.d(DEBUG_TAG, "onCreateLoader");
        if (MyUtils.isCollectedMoviesShowing(getContext())) {
            //show collected
            Set<String> collectedMovieIds = MyUtils.getCollectedMovieIds(getContext());
            List<String> urls = new ArrayList<>();
            if (collectedMovieIds != null && collectedMovieIds.size() > 0) {
                for (String movieId : collectedMovieIds) {
                    String urlStr = "https://api.themoviedb.org/3/movie/"
                            + movieId + "?api_key=abc9d273fe2afffe7d8b56710a96ae15&language=zh";
                    urls.add(urlStr);
                }
            } else {
                Toast.makeText(getContext(), "There is no movie collected!", Toast.LENGTH_SHORT).show();
            }
            return new MovieListAsyncTaskLoader(getContext(), urls);
        }

        // create a loader here, if there isn't a existing one with the id
        return new MovieListAsyncTaskLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(DEBUG_TAG, "onLoadFinished");

        cursor = data;
        adapter.swapCursor(data); // this method messes up with the cursor's pointer, moving it down, i dont know why

        if (!MyUtils.isCollectedMoviesShowing(getContext())) {
            // before each insert delete old data
            getContext().getContentResolver().delete(
                    MovieContracts.MovieTable.CONTENT_URI, null, null);

            // insert data to database
            List<ContentValues> valueList = new ArrayList<>();
            data.moveToPosition(-1); // make sure the cursor's pointer is always before first row
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
        adapter.swapCursor(null);
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
                List<String> movieIds = MyUtils.cursorToMovieIdArray(cursor);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("MOVIE_ITEM_ID", movieIds.get(position));
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
        // make the collection menu option being consistent with the movie showing status
        if (MyUtils.isCollectedMoviesShowing(getContext())) {
            menu.findItem(R.id.action_show_collect).setTitle("显示全部");
        } else {
            menu.findItem(R.id.action_show_collect).setTitle("显示已收藏");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                reloadData();
                break;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.action_show_collect:
                if (showCollected) {  //show all
                    item.setTitle("显示已收藏");
                    restoreDataFromDatabase();
                    MyUtils.setShowAll(getContext());
                } else { // show collected
                    item.setTitle("显示全部");
                    MyUtils.setShowCollected(getContext());
                    reloadData();

                }
                showCollected = !showCollected;
                break;
        }
        return true;
    }

    public void showCollectedMovies() {

    }
}
