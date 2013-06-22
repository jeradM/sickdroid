package com.jeradmeisner.sickbeardalpha.data;

import com.jeradmeisner.sickbeardalpha.interfaces.FutureListItem;


public class FutureEpisode extends Episode implements FutureListItem {

    public FutureEpisode(Show show, int season, int episode, String date, String status) {
        super(show, season, episode, date, status);
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