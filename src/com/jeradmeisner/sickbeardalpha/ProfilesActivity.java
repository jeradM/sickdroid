package com.jeradmeisner.sickbeardalpha;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeradmeisner.sickbeardalpha.fragments.AddProfileFragment;

import java.util.List;


public class ProfilesActivity extends SherlockFragmentActivity implements AddProfileFragment.AddProfileDialogListener {

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
        getSupportMenuInflater().inflate(R.menu.profiles_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_profile:
                addProfile();
                return true;
            default:
                super.onOptionsItemSelected(item);
        }

        return false;
    }

    public void addProfile()
    {
        SherlockDialogFragment addProfileDialog = new AddProfileFragment();
        addProfileDialog.show(getSupportFragmentManager(), "addProfile");
        /*profiles.addProfile(this, "sickbeard", "192.168.1.151", "8081", "sickbeard", "1871f40ea3a3f1b55182d6033ae7062a", false);
        profiles.findProfile("sickbeard").setProfile();
        setResult(RESULT_OK, null);
        finish();*/

    }

    public void onAddProfile(String[] info, boolean https)
    {
        profiles.addProfile(this, info[0], info[1], info[2], info[3], info[4], https);
        profileAdapter.notifyDataSetChanged();
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