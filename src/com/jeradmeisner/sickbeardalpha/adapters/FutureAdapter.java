package com.jeradmeisner.sickbeardalpha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jeradmeisner.sickbeardalpha.R;
import com.jeradmeisner.sickbeardalpha.data.FutureEpisode;
import com.jeradmeisner.sickbeardalpha.interfaces.FutureListItem;
import com.jeradmeisner.sickbeardalpha.widgets.FutureSectionHeader;

import java.util.List;


public class FutureAdapter extends ArrayAdapter<FutureListItem> {

    private Context context;
    private List<FutureListItem> items;

    public FutureAdapter(Context context, int resource, List<FutureListItem> items)
    {
        super(context, resource, items);

        this.context = context;
        this.items = items;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).isHeader()) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItemViewType(position) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout futureView;
        FutureListItem item = getItem(position);


        if (getItemViewType(position) == 0) {

            if (convertView == null) {
                futureView = new LinearLayout(getContext());
                LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                li.inflate(R.layout.future_section_header, futureView, true);
            }
            else {
                futureView = (LinearLayout)convertView;
            }

            TextView tv = (TextView)futureView.findViewById(R.id.future_section);
            tv.setText(((FutureSectionHeader) item).getTitle());

        }
        else {

            if (convertView == null) {
                futureView = new LinearLayout(getContext());
                LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                li.inflate(R.layout.future_list_item, futureView, true);
            }
            else {
                futureView = (LinearLayout)convertView;
            }

                ImageView iv = (ImageView)futureView.findViewById(R.id.future_item);
                iv.setImageBitmap(((FutureEpisode)item).getShow().getBannerImage());

        }

        return futureView;
    }
}
