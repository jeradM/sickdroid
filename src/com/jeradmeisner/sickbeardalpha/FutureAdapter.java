package com.jeradmeisner.sickbeardalpha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jeradmeisner.sickbeardalpha.widgets.FutureSectionHeader;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/17/13
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class FutureAdapter extends ArrayAdapter<FutureItem> {

    private Context context;
    private List<FutureItem> items;

    public FutureAdapter(Context context, int resource, List<FutureItem> items)
    {
        super(context, resource, items);

        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout futureView;
        FutureItem item = getItem(position);

        if (item.isHeader()) {

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
            iv.setImageBitmap(item.getShow().getBannerImage());
        }

        return futureView;
    }
}
