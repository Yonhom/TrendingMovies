package com.xuyonghong.trendingmovies.settings;


import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.xuyonghong.trendingmovies.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the resource into the settings activity
        addPreferencesFromResource(R.xml.pref_general);

        findPreference(getString(R.string.movie_ranking_type_key)).setOnPreferenceChangeListener(this);

        // bind the summary to the preference
        bindSummaryValueToPreference(findPreference(getString(R.string.movie_ranking_type_key)));

    }

    private void bindSummaryValueToPreference(Preference preference) {
        if(preference.getOnPreferenceChangeListener() == null){
            preference.setOnPreferenceChangeListener(this);
        }

        onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.movie_ranking_type_key),"")
        );
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String summaryStr = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(summaryStr);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // enable the action bar's up button to return to home screen
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
