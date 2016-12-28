package com.xuyonghong.trendingmovies.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import com.xuyonghong.trendingmovies.adapter.ImageAdapter;
import com.xuyonghong.trendingmovies.loader.MyAsyncTaskLoader;
import com.xuyonghong.trendingmovies.model.Movie;
import com.xuyonghong.trendingmovies.provider.MovieContracts;
import com.xuyonghong.trendingmovies.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.BACKDROP_PATH;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.OVERVIEW;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.POSTER_PATH;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.RELEASE_DATE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.TITLE;
import static com.xuyonghong.trendingmovies.provider.MovieContracts.MovieTable.VOTE_AVERAGE;


/**
 * this fragment is for the movie list
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List> {

    private static final String DEBUG_TAG = MainFragment.class.getSimpleName();

    private GridView movieGrid; // for displaying movie

    private Date last_update_time = new Date(); // the lastest update time

    private static final int UPDATE_INTERVAL = 30 * 60 * 1000; // 30mins
    
    private String lastRankingOrder = "popular";

    private Loader fetchOnlineMovieListLoader;

    private static final int FETCH_ONLINE_MOVIE_LIST_LOADER = 0;

    private List<Movie> movieList = new ArrayList<>();

    private ImageAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);

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

        // init the MyAsyncTaskLoader
        fetchOnlineMovieListLoader = getLoaderManager().initLoader(0, null, this);
    }


    /// loader related methods, those methods will be called as the loader's stage changes
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // create a loader here, if there isn't a existing one with the id
        return new MyAsyncTaskLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List> loader, List data) {
        if (loader.getId() == FETCH_ONLINE_MOVIE_LIST_LOADER) {
            movieList.addAll(data);
            adapter.notifyDataSetChanged();
            if (movieList.size() > 0) {
                // add data to movie data base
                ContentValues[] values = new ContentValues[movieList.size()];
                for (int i = 0; i < movieList.size(); i++) {
                    Movie movie = (Movie) movieList.get(i);
                    ContentValues value = new ContentValues();
                    value.put(POSTER_PATH, movie.getPoster_path());
                    value.put(TITLE, movie.getTitle());
                    value.put(BACKDROP_PATH, movie.getBackdrop_path());
                    value.put(RELEASE_DATE, movie.getRelease_date());
                    value.put(VOTE_AVERAGE, movie.getVote_average());
                    value.put(OVERVIEW, movie.getOverview());
                    values[i] = value;
                }
                getContext().getContentResolver().bulkInsert(
                        MovieContracts.MovieTable.CONTENT_URI, values);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
    /// load related methods


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        movieGrid = (GridView)view.findViewById(R.id.movie_grid);

        adapter = new ImageAdapter(movieList, getContext());


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
