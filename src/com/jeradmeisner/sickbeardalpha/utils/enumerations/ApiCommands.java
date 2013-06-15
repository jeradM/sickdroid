package com.jeradmeisner.sickbeardalpha.utils.enumerations;

public enum ApiCommands {

    SHOWS("shows"),
    SHOW("show&tvdbid=%s"),
    BANNER("show.getbanner&tvdbid=%s"),
    POSTER("show.getposter&tvdbid=%s"),
    HISTORY("history&limit=%s&type=%s"),
    SEASONLIST("show.seasonlist&tvdbid=%s");

    private String cmdString;

    private ApiCommands(String cmdString)
    {
        this.cmdString = cmdString;
    }

    public String toString()
    {
        return cmdString;
    }
}