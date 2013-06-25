package com.jeradmeisner.sickdroid.data;

import com.jeradmeisner.sickdroid.interfaces.FutureListItem;


public class FutureEpisode extends Episode implements FutureListItem {

    public FutureEpisode(Show show, String title, int season, int episode, String date) {
        super(show, title, season, episode, date, null);
    }

    @Override
    public String airString() {
        return "Airs on ";
    }

    @Override
    public boolean isHeader() {
        return false;
    }
}
