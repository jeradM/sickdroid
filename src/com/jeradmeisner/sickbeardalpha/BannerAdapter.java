package com.jeradmeisner.sickbeardalpha;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class BannerAdapter extends ArrayAdapter<Show> {

    private Context context;
    private int resource;

    public BannerAdapter(Context context, int resource, List<Show> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }
}
