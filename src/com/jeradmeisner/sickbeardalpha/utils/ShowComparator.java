package com.jeradmeisner.sickbeardalpha.utils;

import com.jeradmeisner.sickbeardalpha.data.Show;

import java.util.Comparator;

public class ShowComparator implements Comparator<Show> {

    public int compare(Show showA, Show showB) {
        String strA = showA.toString();
        String strB = showB.toString();

        if (strA.startsWith("The ")) {
            strA = strA.replace("The ", "");
        }

        if (strB.startsWith("The ")) {
            strB = strB.replace("The ", "");
        }

        return strA.compareToIgnoreCase(strB);
    }


}