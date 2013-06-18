package com.jeradmeisner.sickbeardalpha;

import com.jeradmeisner.sickbeardalpha.Show;
import com.jeradmeisner.sickbeardalpha.interfaces.FutureListItem;


public class FutureItem implements FutureListItem {

    private Show show;
    private int season;
    private int episode;
    private String date;

    public FutureItem(Show show, int season, int episode, String date)
    {
        this.show = show;
        this.season = season;
        this.episode = episode;
        this.date = date;
    }

    public Show getShow() {
        return show;
    }

    public int getSeason() {
        return season;
    }

    public int getEpisode() {
        return episode;
    }

    public String getDate() {
        return date;
    }

    public boolean isHeader()
    {
        return false;
    }
}
