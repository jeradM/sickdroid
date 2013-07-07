package com.jeradmeisner.sickdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jeradmeisner.sickdroid.R;
import com.jeradmeisner.sickdroid.data.Season;
import com.jeradmeisner.sickdroid.data.SeasonEpisode;

import java.util.ArrayList;
import java.util.List;


public class SeasonListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Season> seasons;

    public SeasonListAdapter(Context context, List<Season> seasons) {
        this.context = context;
        this.seasons = seasons;
    }


    @Override
    public int getGroupCount() {
        return seasons.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return seasons.get(groupPosition).getEpisodeCount();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return seasons.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return seasons.get(groupPosition).getEpisode(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LinearLayout view;

        if (convertView == null) {
            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (LinearLayout)li.inflate(R.layout.season_header, null);
        }
        else {
            view = (LinearLayout)convertView;
        }

        Season season = seasons.get(groupPosition);
        TextView tv = (TextView)view.findViewById(R.id.season_header_title);
        tv.setText(season.toString());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView tv = new TextView(context);

        SeasonEpisode ep = (SeasonEpisode)seasons.get(groupPosition).getEpisode(childPosition);
        tv.setText(ep.getTitle());
        return tv;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
