package com.jeradmeisner.sickdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jeradmeisner.sickdroid.R;
import com.jeradmeisner.sickdroid.data.Show;
import com.jeradmeisner.sickdroid.utils.BannerCacheManager;
import com.jeradmeisner.sickdroid.widgets.BannerImageView;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends ArrayAdapter<Show> {

    int resource;
    Context context;
    private List<Show> shows, orig;
    private ShowFilter showFilter;
    private LayoutInflater li;
    BannerCacheManager bcm;



    public BannerAdapter (Context context, int resource, List<Show> shows)
    {
        super(context, resource, shows);
        this.resource = resource;
        this.context = context;
        this.shows = shows;
        orig = new ArrayList<Show>(this.shows);
        String inflator = Context.LAYOUT_INFLATER_SERVICE;
        li = (LayoutInflater)getContext().getSystemService(inflator);
        bcm = BannerCacheManager.getInstance(context);

    }

    static class BannerViewHolder {
        BannerImageView image;
        TextView title;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BannerViewHolder holder;
        Show show = getItem(position);

        if (convertView == null) {
            convertView = li.inflate(resource, parent, false);

            holder = new BannerViewHolder();
            holder.image = (BannerImageView)convertView.findViewById(R.id.banner_image_view);
            holder.title = (TextView)convertView.findViewById(R.id.banner_text_view);

            convertView.setTag(holder);
        }
        else {
            holder = (BannerViewHolder)convertView.getTag();
        }



        if (show != null) {
            holder.image.setBannerImage(show.getTvdbid());
            holder.title.setText(show.getTitle());
        }

        return convertView;
    }

    @Override
    public Filter getFilter()
    {
        if (showFilter == null)
            showFilter = new ShowFilter();
        return showFilter;
    }

    public class ShowFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraints)
        {
            FilterResults results = new FilterResults();

            if (constraints == null || constraints.length() == 0) {
                results.values = orig;
                results.count = orig.size();
            }
            else {
                List<Show> values = new ArrayList<Show>();

                for (Show show : orig) {
                    if (show.getTitle().toLowerCase().contains(constraints.toString().toLowerCase())) {
                        values.add(show);
                    }
                }
                results.values = values;
                results.count = values.size();
            }

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraints, FilterResults results)
        {
            clear();
            for (Show show : (List<Show>)results.values) {
                add(show);
            }
            notifyDataSetChanged();
        }
    }
}
