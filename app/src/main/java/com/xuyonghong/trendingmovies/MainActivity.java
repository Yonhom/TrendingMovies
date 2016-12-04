package com.xuyonghong.trendingmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.xuyonghong.trendingmovies.fragment.MainFragment;


public class MainActivity extends AppCompatActivity { // AppCompatActivity: add the action bar functionality
    private static final String DEBUG_TAG = MainActivity.class.getSimpleName();

    private static final String MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // add the fragment only once
        if (savedInstanceState == null) {
            // add the main fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main, new MainFragment(), MAIN_FRAGMENT_TAG).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu); // inflate the xml into the memory

        return super.onCreateOptionsMenu(menu);
    }


}
