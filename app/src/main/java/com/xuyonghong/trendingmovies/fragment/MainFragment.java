package com.xuyonghong.trendingmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.xuyonghong.trendingmovies.adapter.ImageAdapter;
import com.xuyonghong.trendingmovies.bean.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * this fragment is for the movie list
 */
public class MainFragment extends Fragment {

    private static final String DEBUG_TAG = MainFragment.class.getSimpleName();

    private static final String TRENDING_MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/popular?api_key=abc9d273fe2afffe7d8b56710a96ae15";
    private static final String TOP_RATED_MOVIE_REQUEST_URL =
            "https://api.themoviedb.org/3/movie/top_rated?api_key=abc9d273fe2afffe7d8b56710a96ae15";

    private List<Movie> movieArray = null;

    private GridView movieGrid; // for displaying movie

    public MainFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFragment();
    }


    private void updateFragment() {
        new MyAsyncTask().execute(TRENDING_MOVIE_REQUEST_URL);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            ConnectivityManager cManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cManager.getActiveNetworkInfo();

            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                // internet connected, fetch data
                String responseStr = getResponseFromReqUrl(params[0]);

                if (responseStr != null) {
                    movieArray = movieJsonToArray(responseStr);
                }



            } else {
                // remind the user that there is internet connectivity problem
                Toast.makeText(
                        getActivity(),
                        "Mayday, mayday! The internet is down, now we are all in peril!!",
                        Toast.LENGTH_SHORT)
                        .show();;
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (movieArray.size() > 0) {
                ImageAdapter adapter = new ImageAdapter(movieArray, getActivity());
                movieGrid.setAdapter(adapter);
            }
        }

        private List<Movie> movieJsonToArray(String jsonStr) {
            ArrayList<Movie> movieArray = new ArrayList<>();
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray results = jsonObj.getJSONArray("results");
                for(int i = 0; i < results.length(); i++) {
                    JSONObject jsonObject = results.getJSONObject(i); // the individual json object

                    Movie movie = new Movie();
                    movie.setPoster_path(jsonObject.getString("poster_path"));
                    movie.setTitle(jsonObject.getString("title"));
                    movie.setBackdrop_path(jsonObject.getString("backdrop_path"));
                    movie.setRelease_date(jsonObject.getString("release_date"));
                    movie.setVote_average(jsonObject.getString("vote_average"));
                    movie.setOverview(jsonObject.getString("overview"));

                    movieArray.add(movie);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(DEBUG_TAG, movieArray.toString());
            return movieArray;
        }


        @Nullable
        private String getResponseFromReqUrl(String urlStr) {
            InputStream is = null;
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);  // default is true
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    Toast.makeText(getActivity(),
                            "Response code is" + responseCode + ", not 200",
                            Toast.LENGTH_SHORT).show();
                    return null;
                }

                is = conn.getInputStream();
                if (is == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line;

                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    return null;
                }
                String responseStr = buffer.toString();
                Log.d(DEBUG_TAG, responseStr);

                return responseStr;
            }catch(IOException e){
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        movieGrid = (GridView)view.findViewById(R.id.movie_grid);

        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("MOVIE_ITEM", movieArray.get(position));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            updateFragment();
        }
        return super.onOptionsItemSelected(item);
    }
}
