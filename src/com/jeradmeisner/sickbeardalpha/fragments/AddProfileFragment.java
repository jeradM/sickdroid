package com.jeradmeisner.sickbeardalpha.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.jeradmeisner.sickbeardalpha.R;

public class AddProfileFragment extends SherlockDialogFragment {

    private final String TAG = "AddProfileFragment";

    private EditText editName;
    private EditText editHost;
    private EditText editPort;
    private EditText editWebroot;
    private EditText editApikey;
    private CheckBox httpsCheckbox;

    public interface AddProfileDialogListener {
        public void onAddProfile(String[] hostInfo, boolean https);
    }

    private AddProfileDialogListener mListener;


    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_profile, container);
        editHost = (EditText)view.findViewById(R.id.add_host);
        editPort = (EditText)view.findViewById(R.id.add_port);
        editWebroot = (EditText)view.findViewById(R.id.add_webroot);
        editApikey = (EditText)view.findViewById(R.id.add_apikey);
        httpsCheckbox = (CheckBox)view.findViewById(R.id.add_usehttps);

        getDialog().setTitle(getResources().getString(R.string.add_profile_string));
        return view;
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (AddProfileDialogListener)activity;
        }
        catch (ClassCastException e) {
            Log.e(TAG, "Calling activity must implement AddProfileDialogListener.");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.fragment_add_profile, null);

        editName = (EditText)view.findViewById(R.id.add_name);
        editHost = (EditText)view.findViewById(R.id.add_host);
        editPort = (EditText)view.findViewById(R.id.add_port);
        editWebroot = (EditText)view.findViewById(R.id.add_webroot);
        editApikey = (EditText)view.findViewById(R.id.add_apikey);
        httpsCheckbox = (CheckBox)view.findViewById(R.id.add_usehttps);

        builder.setTitle(getResources().getString(R.string.add_profile_string));
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editName.getText().toString();
                        String host = editHost.getText().toString();
                        String port = editPort.getText().toString();
                        String webroot = editWebroot.getText().toString();
                        String apikey = editApikey.getText().toString();
                        boolean https = httpsCheckbox.isChecked();

                        if (name.length() > 0 && host.length() > 0 && port.length() > 0 && apikey.length() > 0) {
                            String[] hostInfo = {name, host, port, webroot, apikey};
                            mListener.onAddProfile(hostInfo, https);
                        } else {
                            Toast.makeText(getActivity(), "Invalid Settings", 2000).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

}
