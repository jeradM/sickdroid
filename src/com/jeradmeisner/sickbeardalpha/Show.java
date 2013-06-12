package com.jeradmeisner.sickbeardalpha;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Show implements Parcelable {

    private String tvdbid;
    private String title;
    private String network;
    private String quality;
    private String status;
    private String language;
    private String nextEpisodeAirDate;

    private int airByDate;
    private int banner;
    private int poster;
    private int paused;

    private Bitmap bannerImage;
    private Bitmap posterImage;

    public Show(String tvdbid, String title, String network, String quality,
                String status, String language, String nextEpisodeAirDate,
                int airByDate, int banner, int poster, int paused)
    {
        this.tvdbid = tvdbid;
        this.title = title;
        this.network = network;
        this.quality = quality;
        this.status = status;
        this.language = language;
        this.nextEpisodeAirDate = nextEpisodeAirDate;
        this.airByDate = airByDate;
        this.banner = banner;
        this.poster = poster;
        this.paused = paused;
    }

    public Show(Parcel parcel)
    {
        tvdbid = parcel.readString();
        title = parcel.readString();
        network = parcel.readString();
        quality = parcel.readString();
        status = parcel.readString();
        language = parcel.readString();
        nextEpisodeAirDate = parcel.readString();
        airByDate = parcel.readInt();
        banner = parcel.readInt();
        poster = parcel.readInt();
        paused = parcel.readInt();
    }


    public String getTvdbid() {
        return tvdbid;
    }

    public String getTitle() {
        return title;
    }

    public String getNetwork() {
        return network;
    }

    public String getQuality() {
        return quality;
    }

    public String getStatus() {
        return status;
    }

    public String getLanguage() {
        return language;
    }

    public String getNextEpisodeAirDate() {
        return nextEpisodeAirDate;
    }

    public int hasAirByDate() {
        return airByDate;
    }

    public int hasBanner() {
        return banner;
    }

    public int hasPoster() {
        return poster;
    }

    public int hasPaused() {
        return paused;
    }

    public Bitmap getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(Bitmap bannerImage)
    {
        this.bannerImage = bannerImage;
    }

    public void setPosterImage(Bitmap posterImage)
    {
        this.posterImage = posterImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tvdbid);
        parcel.writeString(title);
        parcel.writeString(network);
        parcel.writeString(quality);
        parcel.writeString(status);
        parcel.writeString(language);
        parcel.writeString(nextEpisodeAirDate);
        parcel.writeInt(airByDate);
        parcel.writeInt(banner);
        parcel.writeInt(poster);
        parcel.writeInt(paused);
    }

    public static Creator<Show> CREATOR = new Creator<Show>() {
        public Show createFromParcel(Parcel parcel) {
            return new Show(parcel);
        }

        public Show[] newArray(int size) {
            return new Show[size];
        }
    };
}