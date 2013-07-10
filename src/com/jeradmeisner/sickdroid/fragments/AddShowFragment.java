package com.jeradmeisner.sickdroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
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

        Spinner quality = (Spinner)view.findViewById(R.id.quality_spinner);
        Spinner status = (Spinner)view.findViewById(R.id.status_spinner);
        CheckBox flatten = (CheckBox)view.findViewById(R.id.flatten_folders);

        String[] qualityList = {"SD", "HD720", "HD1080", "Any"};
        ArrayAdapter<String> qAdapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_list_item_1, qualityList);
        quality.setAdapter(qAdapter);

        String[] statusList = {"Skipped", "Wanted", "Ignored"};
        ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(getSherlockActivity(), android.R.layout.simple_list_item_1, statusList);
        status.setAdapter(sAdapter);

        setStyle(SherlockDialogFragment.STYLE_NO_TITLE, R.style.Theme_Sickdroid_LightGreen);

        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setView(view);

        return builder.create();
    }
}
