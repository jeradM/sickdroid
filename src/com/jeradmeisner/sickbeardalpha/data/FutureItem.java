package com.jeradmeisner.sickbeardalpha.data;

import com.jeradmeisner.sickbeardalpha.data.HistoryEpisode;
import com.jeradmeisner.sickbeardalpha.data.Show;
import com.jeradmeisner.sickbeardalpha.interfaces.FutureListItem;


public class FutureItem extends HistoryEpisode implements FutureListItem {

    public FutureItem(Show show, String season, String episode, String date)
    {
        super(show, 2, 2, date, null);
    }

    public boolean isHeader()
    {
        return false;
    }
}
