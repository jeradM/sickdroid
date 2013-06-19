package com.jeradmeisner.sickbeardalpha;

import com.jeradmeisner.sickbeardalpha.Show;
import com.jeradmeisner.sickbeardalpha.interfaces.FutureListItem;


public class FutureItem extends HistoryItem implements FutureListItem {

    public FutureItem(Show show, String season, String episode, String date)
    {
        super(show, season, episode, date);
    }

    public boolean isHeader()
    {
        return false;
    }
}
