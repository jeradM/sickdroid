package com.jeradmeisner.sickbeardalpha.data;


public abstract class Episode {

    protected Show show;
    protected int season;
    protected int episode;
    protected String date;
    protected String status;
    protected String description;

    protected Episode(Show show, int season, int episode, String date, String status) {
        this.show = show;
        this.season = season;
        this.episode = episode;
        this.date = date;
        this.status = status;
    }

    public abstract String airString();

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

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
