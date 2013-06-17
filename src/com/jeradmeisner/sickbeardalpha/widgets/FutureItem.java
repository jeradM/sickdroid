package com.jeradmeisner.sickbeardalpha.widgets;

import com.jeradmeisner.sickbeardalpha.Show;


public class FutureItem {

    private Show show;
    private String season;
    private String episode;
    private String date;

    public FutureItem(Show show, String season, String episode, String date)
    {
        this.show = show;
        this.season = season;
        this.episode = episode;
        this.date = date;
    }

    public Show getShow() {
        return show;
    }

    public String getSeason() {
        return season;
    }

    public String getEpisode() {
        return episode;
    }

    public String getDate() {
        return date;
    }
}
