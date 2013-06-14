package com.jeradmeisner.sickbeardalpha.fragments;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.jeradmeisner.sickbeardalpha.R;

public class AddProfileFragment extends SherlockDialogFragment {

    private EditText editHost;
    private EditText editPort;
    private EditText editWebroot;
    private EditText editApikey;
    private CheckBox httpsCheckbox;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_profile, container);
        editHost = (EditText)view.findViewById(R.id.add_host);
        editPort = (EditText)view.findViewById(R.id.add_port);
        editWebroot = (EditText)view.findViewById(R.id.add_webroot);
        editApikey = (EditText)view.findViewById(R.id.add_apikey);
        httpsCheckbox = (CheckBox)view.findViewById(R.id.add_usehttps);

        getDialog().setTitle(getResources().getString(R.string.add_profile_string));
        return null;
    }

    public interface AddProfileDialogListener {
        public void onAddProfile(String[] hostInfo, boolean https);
    }

    private AddProfileDialogListener mListener;
}
