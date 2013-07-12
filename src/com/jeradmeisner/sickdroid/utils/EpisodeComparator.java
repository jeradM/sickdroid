package com.jeradmeisner.sickdroid.utils;

import com.jeradmeisner.sickdroid.data.Episode;

import java.util.Comparator;

public class EpisodeComparator implements Comparator<Episode> {

    @Override
    public int compare(Episode episodeA, Episode episodeB) {

        int a = episodeA.getEpisode();
        int b = episodeB.getEpisode();

        if (a == b)
            return 0;
        else if (a < b)
            return 1;
        else
            return -1;
    }
}
