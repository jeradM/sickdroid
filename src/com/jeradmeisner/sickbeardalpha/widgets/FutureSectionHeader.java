package com.jeradmeisner.sickbeardalpha.widgets;

import com.jeradmeisner.sickbeardalpha.FutureItem;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/17/13
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class FutureSectionHeader extends FutureItem {

    private String title;

    public FutureSectionHeader(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
}
