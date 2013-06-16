package com.jeradmeisner.sickbeardalpha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends ArrayAdapter<Show> {

    int resource;
    Context context;
    private List<Show> shows;
    private ShowFilter showFilter;

    public BannerAdapter (Context context, int resource, List<Show> shows)
    {
        super(context, resource, shows);
        this.resource = resource;
        this.context = context;
        this.shows = shows;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RelativeLayout bannerView;
        Show show = getItem(position);

        if (convertView == null) {
            bannerView = new RelativeLayout(getContext());
            String inflator = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflator);
            li.inflate(resource, bannerView, true);
        }
        else {
            bannerView = (RelativeLayout)convertView;
        }


        ImageView imageView = (ImageView)bannerView.findViewById(R.id.banner_image_view);
        TextView textView = (TextView)bannerView.findViewById(R.id.banner_text_view);

        imageView.setImageBitmap(show.getBannerImage());
        textView.setText(show.getTitle());

        return bannerView;
    }

    @Override
    public Filter getFilter() {
        if (showFilter == null) {
            showFilter = new ShowFilter();
        }
        return showFilter;
    }

    public class ShowFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraints) {
            FilterResults results = new FilterResults();

            if (constraints == null || constraints.length() == 0) {
                results.values = shows;
                results.count = shows.size();
            }
            else {
                List<Show> values = new ArrayList<Show>();

                for (Show show : shows) {
                    if (show.getTitle().toLowerCase().startsWith(constraints.toString().toLowerCase())) {
                        values.add(show);
                    }
                }
                results.values = values;
                results.count = values.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraints, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
            }
            else {
                shows = (List<Show>)results.values;
            }
        }
    }
}
