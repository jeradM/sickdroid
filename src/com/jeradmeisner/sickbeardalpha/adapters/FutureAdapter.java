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
import com.jeradmeisner.sickbeardalpha.data.FutureItem;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout futureView;
        FutureListItem item = getItem(position);

        futureView = new LinearLayout(getContext());
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (item.isHeader()) {

            li.inflate(R.layout.future_section_header, futureView, true);

            TextView tv = (TextView)futureView.findViewById(R.id.future_section);
            tv.setText(((FutureSectionHeader)item).getTitle());
        }
        else {
            li.inflate(R.layout.future_list_item, futureView, true);

            ImageView iv = (ImageView)futureView.findViewById(R.id.future_item);
            iv.setImageBitmap(((FutureItem)item).getShow().getBannerImage());
        }

        return futureView;
    }
}
