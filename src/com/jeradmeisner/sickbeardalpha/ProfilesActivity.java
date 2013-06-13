package com.jeradmeisner.sickbeardalpha;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/12/13
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProfilesActivity extends Activity {

    private ListView profileListView;
    private SickbeardProfiles profiles;
    private List<SickbeardProfile> profileList;
    private ArrayAdapter<SickbeardProfile> profileAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        profileListView = (ListView)findViewById(R.id.profiles_list_view);
        profiles = SickbeardProfiles.getInstance();

        new LoadProfilesTask().execute(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private class LoadProfilesTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            profiles.loadProfiles(ProfilesActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            profileList = profiles.getProfiles();
            profileAdapter = new ArrayAdapter<SickbeardProfile>(ProfilesActivity.this, android.R.layout.simple_list_item_1, profileList);
            profileListView.setAdapter(profileAdapter);
            profileAdapter.notifyDataSetChanged();

        }
    }
}