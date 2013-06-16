package com.jeradmeisner.sickbeardalpha.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.jeradmeisner.sickbeardalpha.Show;
import com.jeradmeisner.sickbeardalpha.utils.enumerations.ApiCommands;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Celestina
 * Date: 6/15/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArtworkDownloader {

    public static Bitmap fetchBanner(String urlstring, Show show, int maxWidth) throws IOException
    {
        String command = String.format(ApiCommands.BANNER.toString(), show.getTvdbid());
        URL url = new URL(urlstring + "?cmd=" + command);
        InputStream is = getInputStream(url);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        int width = options.outWidth;
        final int widthRatio = Math.round((float) width / (float) maxWidth);
        options.inSampleSize = widthRatio;
        options.inJustDecodeBounds = false;
        is = getInputStream(url);
        return BitmapFactory.decodeStream(is, null, options);
    }

    public static Bitmap fetchPoster(String urlstring, Show show, int maxHeight) throws IOException
    {
        String command = String.format(ApiCommands.POSTER.toString(), show.getTvdbid());
        URL url = new URL(urlstring + "?cmd=" + command);
        InputStream is = getInputStream(url);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        int height = options.outHeight;
        final int heightRatio = Math.round((float) height / (float) maxHeight);
        options.inSampleSize = heightRatio;
        options.inJustDecodeBounds = false;
        is = getInputStream(url);
        return BitmapFactory.decodeStream(is, null, options);
    }

    public static Bitmap fetchFanart(String tvdbid, int maxWidth) throws IOException
    {
        return TVDBFanartDownloader.fetchFanart(tvdbid, maxWidth);
    }

    private static InputStream getInputStream(URL url) throws IOException
    {
        HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
        urlConn.setDoInput(true);
        urlConn.connect();
        InputStream is = urlConn.getInputStream();
        return is;
    }
}
