package com.jeradmeisner.sickbeardalpha.widgets;

import com.jeradmeisner.sickbeardalpha.interfaces.FutureListItem;


public class FutureSectionHeader implements FutureListItem {

    private String title;

    public FutureSectionHeader(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean isHeader()
    {
        return true;
    }
}
