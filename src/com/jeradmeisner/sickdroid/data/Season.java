package com.jeradmeisner.sickdroid.data;

import java.util.ArrayList;
import java.util.List;


public class Season {

    private List<SeasonEpisode> episodes;

    public Season()
    {
        episodes = new ArrayList<SeasonEpisode>();
    }

    public void addEpisode(SeasonEpisode episode)
    {
        episodes.add(episode);
    }
}
