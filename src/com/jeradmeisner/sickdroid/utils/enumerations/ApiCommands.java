package com.jeradmeisner.sickdroid.utils.enumerations;

public enum ApiCommands {

    SHOWS("shows"),
    SHOW("show&tvdbid=%s"),
    BANNER("show.getbanner&tvdbid=%s"),
    POSTER("show.getposter&tvdbid=%s"),
    HISTORY("history&limit=%s&type=%s"),
    SEASONLIST("show.seasonlist&tvdbid=%s"),
    EPISODE("episode&tvdbid=%s&season=%s&episode=%s"),
    EPISODE_SEARCH("episode.search&tvdbid=%s&season=%s&episode=%s"),
    FUTURE("future"),
    SEASONS("show.seasons&tvdbid=%s"),
    ADD_SHOW("show.addnew&tvdbid=%s&initial=%s&status=%s&flatten_folders=%s");

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