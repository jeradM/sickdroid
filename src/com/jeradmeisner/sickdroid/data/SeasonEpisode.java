package com.jeradmeisner.sickdroid.data;


public class SeasonEpisode extends Episode {

    private String quality;

    public SeasonEpisode(Show show, String title, int season, int episode, String date, String status, String quality) {
        super(show, title, season, episode, date, status);
        this.quality = quality;
    }

    @Override
    public String airString() {
        return "Airdate: ";
    }

    public String getQuality() {
        return quality;
    }
}
