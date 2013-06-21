package com.jeradmeisner.sickbeardalpha.data;


/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/14/13
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryEpisode extends Episode {

    public HistoryEpisode(Show show, int season, int episode, String date, String status) {
        super(show, season, episode, date, status);
    }

    @Override
    public String airString() {
        return "Aired on " + date;
    }
}
