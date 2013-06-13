package com.jeradmeisner.sickbeardalpha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

public class BannerAdapter extends ArrayAdapter<Show> {

    int resource;
    Context context;

    public BannerAdapter (Context context, int resource, List<Show> shows)
    {
        super(context, resource, shows);
        this.resource = resource;
        this.context = context;
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
}
