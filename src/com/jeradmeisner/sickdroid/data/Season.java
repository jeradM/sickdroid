package com.jeradmeisner.sickdroid.data;

import java.util.ArrayList;
import java.util.List;


public class Season {

    private List<Episode> episodes;

    public Season()
    {
        episodes = new ArrayList<Episode>();
    }

    public void addEpisode(Episode episode)
    {
        episodes.add(episode);
    }
}
