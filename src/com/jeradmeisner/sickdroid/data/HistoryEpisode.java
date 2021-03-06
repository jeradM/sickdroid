package com.jeradmeisner.sickdroid.data;


/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/14/13
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryEpisode extends Episode {

    private String airDate;

    public HistoryEpisode(Show show, String title, int season, int episode, String date, String status) {
        super(show, title, season, episode, date, status);
    }

    @Override
    public String airString() {
        return "Aired on ";
    }

    public void setAirDate(String airDate)
    {
        this.airDate = airDate;
    }

    public String getDate() {
        return airDate;
    }

    public String getDownloadedDate()
    {
        return date;
    }
}
