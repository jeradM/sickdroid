package com.jeradmeisner.sickdroid.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Shows implements Serializable
{

    private List<Show> showList;
    private JSONObject showsJson;

    public Shows(List<Show> shows)
    {
        this.showList = shows;
    }

    public Shows(JSONObject json)
    {
        showList = new ArrayList<Show>();
        showsJson = json;
        buildList();
    }

    /**
     * Build a list of shows by parsing a JSONObject of shows information
     */
    private void buildList()
    {
        showList.clear();
        Iterator<?> itr = showsJson.keys();
        while(itr.hasNext()) {
            try {
                String id = itr.next().toString();
                JSONObject nextObject = showsJson.getJSONObject(id);
                String title = nextObject.getString("show_name");
                String network = nextObject.getString("network");
                String quality = nextObject.getString("quality");
                String status = nextObject.getString("status");
                String language = nextObject.getString("language");
                String nextEp = nextObject.getString("next_ep_airdate");

                int airByDate = nextObject.getInt("air_by_date");
                int paused = nextObject.getInt("paused");

                JSONObject cache = nextObject.getJSONObject("cache");
                int banner = cache.getInt("banner");
                int poster = cache.getInt("poster");

                showList.add(new Show(id, title, network, quality, status, language, nextEp, airByDate, banner, poster, paused));
            }
            catch (JSONException e) {
                Log.e("Show Builder", "Error parsing JSON information");
                showList = null;
            }
        }
    }

    public void refreshShows(JSONObject json)
    {
        showsJson = json;
        buildList();
    }

    public Show findShow(String tvdbid)
    {
        for (Show show : showList) {
            if (tvdbid.equals(show.getTvdbid())) {
                return show;
            }
        }

        return null;
    }

    /**
     * Gets the list of shows
     *
     * @return the list of shows
     *
     * @throws ShowListException if there was an error parsing the show JSON
     */
    public List<Show> getShowList()
    {
        return showList;
    }

    public class ShowListException extends Exception {
        static final long serialVersionUID = 1;
    }

}