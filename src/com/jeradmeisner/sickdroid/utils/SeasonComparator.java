package com.jeradmeisner.sickdroid.utils;

import com.jeradmeisner.sickdroid.data.Season;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: celestina
 * Date: 7/6/13
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeasonComparator implements Comparator<Season> {

    @Override
    public int compare(Season a, Season b) {
        String aStr = a.toString();
        String bStr = b.toString();


        if (aStr.length() == bStr.length() || aStr.equals("Specials") || bStr.equals("Specials")) {
            return aStr.compareToIgnoreCase(bStr);
        }
        else {
            if (aStr.length() < bStr.length()) {
                return -1;
            }
            else {
                return 1;
            }
        }
    }
}
