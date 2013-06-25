package com.jeradmeisner.sickdroid.widgets;

import com.jeradmeisner.sickdroid.interfaces.FutureListItem;


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
