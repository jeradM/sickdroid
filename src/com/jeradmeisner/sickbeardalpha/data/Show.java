package com.jeradmeisner.sickbeardalpha.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Show implements Parcelable {

    private String tvdbid;
    private String title;
    private String network;
    private String quality;
    private String status;
    private String language;
    private String nextEpisodeAirDate;

    private String overview;

    private int airByDate;
    private int banner;
    private int poster;
    private int paused;

    private int[] seasonList = new int[20];

    private List<Season> seasons = new ArrayList<Season>();

    /*private Bitmap bannerImage;
    private Bitmap posterImage;
*/
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
        overview = parcel.readString();
        airByDate = parcel.readInt();
        banner = parcel.readInt();
        poster = parcel.readInt();
        paused = parcel.readInt();

        int len = parcel.readInt();

        /*bannerImage = parcel.readParcelable(null);
        posterImage = parcel.readParcelable(null);
*/
        for (int i = 0; i < len; i++) {
            seasonList[i] = parcel.readInt();
        }
    }


    public String toString()
    {
        return getTitle();
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

    public void setOverview(String overview)
    {
        this.overview = overview;
    }

    public String getOverview()
    {
        return overview;
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

    public void setSeasonList(int[] seasonList)
    {
        this.seasonList = seasonList;
    }

    public int[] getSeasonList()
    {
        return seasonList;
    }

   /* public Bitmap getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(Bitmap bannerImage)
    {
        this.bannerImage = bannerImage;
    }

    public Bitmap getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(Bitmap posterImage)
    {
        this.posterImage = posterImage;
    }*/

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
        parcel.writeString(overview);
        parcel.writeInt(airByDate);
        parcel.writeInt(banner);
        parcel.writeInt(poster);
        parcel.writeInt(paused);

        parcel.writeInt(seasonList.length);

       /* parcel.writeParcelable(bannerImage, PARCELABLE_WRITE_RETURN_VALUE);
        parcel.writeParcelable(posterImage, PARCELABLE_WRITE_RETURN_VALUE);
*/
        for (int s : seasonList) {
            parcel.writeInt(s);
        }
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