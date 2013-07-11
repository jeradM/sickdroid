package com.jeradmeisner.sickdroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.jeradmeisner.sickdroid.GetNewShowBannerService;
import com.jeradmeisner.sickdroid.R;
import com.jeradmeisner.sickdroid.ShowsActivity;
import com.jeradmeisner.sickdroid.data.TvdbSearchResult;
import com.jeradmeisner.sickdroid.utils.ArtworkDownloader;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;
import com.jeradmeisner.sickdroid.utils.SickbeardJsonUtils;
import com.jeradmeisner.sickdroid.utils.enumerations.ApiCommands;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AddShowFragment extends SherlockDialogFragment {

    private TvdbSearchResult result;
    private String apiurl;

    Spinner quality;
    Spinner status;
    CheckBox flatten;


    Context c;



    public static AddShowFragment getInstance(TvdbSearchResult result, String apiurl) {
        AddShowFragment frag = new AddShowFragment();
        Bundle b = new Bundle(2);
        b.putParcelable("result", result);
        b.putString("apiurl", apiurl);
        frag.setArguments(b);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = getArguments();
        result = b.getParcelable("result");
        apiurl = b.getString("apiurl");

        c = getSherlockActivity();

        LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_show, null);

        TextView titleView = (TextView)view.findViewById(R.id.fragment_title);
        titleView.setText(result.toString());

        quality = (Spinner)view.findViewById(R.id.quality_spinner);
        status = (Spinner)view.findViewById(R.id.status_spinner);
        flatten = (CheckBox)view.findViewById(R.id.flatten_folders);

        ArrayAdapter<CharSequence> qAdapter = ArrayAdapter.createFromResource(getSherlockActivity(), R.array.quality_strings, android.R.layout.simple_spinner_item);
        qAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quality.setAdapter(qAdapter);

        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(getSherlockActivity(), R.array.status_strings, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(sAdapter);

        setStyle(SherlockDialogFragment.STYLE_NO_TITLE, R.style.Theme_Sickdroid_LightGreen);

        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AddShowTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private class AddShowTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String stat = String.valueOf(status.getSelectedItem());
            String qual = String.valueOf(quality.getSelectedItem());
            int flat = (flatten.isChecked() ? 1 : 0);

            String cmd = String.format(ApiCommands.ADD_SHOW.toString(), result.getTvdbid(), qual, stat, flat);
            JSONObject obj = SickbeardJsonUtils.getJsonFromUrl(apiurl, cmd);


            if (obj == null) {
                return "failed";
            }

            try {
                return obj.getString("result");
            } catch (JSONException e) {
                return "failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            String toastText;

            if (s.equals("success")) {
                toastText = "Successfully added " + result.getTitle();
                Intent i = new Intent(c.getApplicationContext(), GetNewShowBannerService.class);
                i.putExtra("apiurl", apiurl);
                i.putExtra("id", result.getTvdbid());
                c.startService(i);
            }
            else {
                toastText = "Failed to add " + result.getTitle();
            }

            int l = Toast.LENGTH_LONG;

            Toast t = Toast.makeText(c.getApplicationContext(), toastText, l);
            t.show();
        }
    }

    private class GetBannerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            BannerCacheManager bcm = BannerCacheManager.getInstance(c);
            int width = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext()).getInt("max_width", 1081);
            int height = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext()).getInt("max_height", 241);

            return null;
        }

        /*@Override
        protected void onPostExecute(Void aVoid) {
            Intent i = new Intent(c, ShowsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }*/
    }
}
