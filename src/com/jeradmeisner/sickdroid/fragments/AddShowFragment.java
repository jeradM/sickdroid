package com.jeradmeisner.sickdroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.jeradmeisner.sickdroid.R;
import com.jeradmeisner.sickdroid.data.TvdbSearchResult;

public class AddShowFragment extends SherlockDialogFragment {

    private TvdbSearchResult result;
    private String apiurl;


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

        LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_show, null);

        TextView titleView = (TextView)view.findViewById(R.id.fragment_title);
        titleView.setText(result.toString());

        final Spinner quality = (Spinner)view.findViewById(R.id.quality_spinner);
        final Spinner status = (Spinner)view.findViewById(R.id.status_spinner);
        final CheckBox flatten = (CheckBox)view.findViewById(R.id.flatten_folders);

        //String[] qualityList = {"SD", "HD720", "HD1080", "Any"};
        ArrayAdapter<CharSequence> qAdapter = ArrayAdapter.createFromResource(getSherlockActivity(), R.array.quality_strings, android.R.layout.simple_spinner_item);
        qAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quality.setAdapter(qAdapter);


        //String[] statusList = {"Skipped", "Wanted", "Ignored"};
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(getSherlockActivity(), R.array.status_strings, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(sAdapter);

        setStyle(SherlockDialogFragment.STYLE_NO_TITLE, R.style.Theme_Sickdroid_LightGreen);

        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String stat = String.valueOf(status.getSelectedItem());
                        String qual = String.valueOf(quality.getSelectedItem());
                        boolean flat = flatten.isChecked();
                        Toast.makeText(getSherlockActivity(), stat + " " + qual + " " + String.valueOf(flat), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }
}
