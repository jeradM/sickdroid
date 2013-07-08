package com.jeradmeisner.sickdroid.data;

import java.util.ArrayList;
import java.util.List;


public class Season {

    private List<SeasonEpisode> episodes;
    private int seasonNumber;

    public Season(int seasonNumber)
    {
        this.seasonNumber = seasonNumber;
        episodes = new ArrayList<SeasonEpisode>();
    }

    public void addEpisode(SeasonEpisode episode)
    {
        episodes.add(episode);
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public List<SeasonEpisode> getEpisodes() {
        return episodes;
    }

    public Episode getEpisode(int epNumber) {
        return episodes.get(epNumber);
    }

    public int getEpisodeCount() {
        return episodes.size();
    }

    @Override
    public String toString() {
        if (seasonNumber == 0)
            return "Specials";
        else
            return "Season " + seasonNumber;
    }
}
