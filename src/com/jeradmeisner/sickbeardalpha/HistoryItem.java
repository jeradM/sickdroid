package com.jeradmeisner.sickbeardalpha;


/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/14/13
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryItem {

    private Show show;
    private String season;
    private String episode;
    private String date;

    public HistoryItem(Show show, String season, String episode, String date)
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
