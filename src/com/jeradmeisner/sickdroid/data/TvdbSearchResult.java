package com.jeradmeisner.sickdroid.data;

import android.os.Parcel;
import android.os.Parcelable;

public class TvdbSearchResult implements Parcelable {

    private String tvdbid;
    private String title;
    private String date;

    public TvdbSearchResult(String tvdbid, String title, String date) {
        this.tvdbid = tvdbid;
        this.title = title;
        this.date = date;
    }

    public TvdbSearchResult(Parcel parcel) {
        tvdbid = parcel.readString();
        title = parcel.readString();
        date = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tvdbid);
        dest.writeString(title);
        dest.writeString(date);
    }

    public static Creator<TvdbSearchResult> CREATOR = new Creator<TvdbSearchResult>() {
        public TvdbSearchResult createFromParcel(Parcel parcel) {
            return new TvdbSearchResult(parcel);
        }

        public TvdbSearchResult[] newArray(int size) {
            return new TvdbSearchResult[size];
        }
    };
}
