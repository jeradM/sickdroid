package com.jeradmeisner.sickdroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import com.jeradmeisner.sickdroid.data.Season;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: celestina
 * Date: 7/6/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeasonListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Season> seasons;

    public SeasonListAdapter(Context context, ArrayList<Season> seasons) {
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
        return groupPosition + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
