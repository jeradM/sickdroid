package com.jeradmeisner.sickdroid.data;



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
