package com.jeradmeisner.sickdroid.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jeradmeisner.sickdroid.data.Show;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TVDBApi {

    public static final String LOG_NAME = "TVDatabaseParser";
    public static final String TVDB_API_KEY = "1A41A145E2DA0053";
    public static final String SERIES_API_URL = "http://thetvdb.com/api/GetSeries.php?seriesname=%s";
    public static final String BANNER_API_URL = "http://www.thetvdb.com/api/%s/series/%s/banners.xml";
    public static final String BANNER_URL_PREFIX = "http://www.thetvdb.com/banners/%s";

    /**
     * Gets the first found fanart for the given tvdbid
     *
     * @param  tvdbid  the tvdbid of the target show
     *
     * @return a fanart image for this show (16:9 ratio)
     */
    public static Bitmap fetchFanart(String tvdbid, int maxWidth) throws IOException
    {
        String urlString = getFanartUrl(tvdbid);

        if (urlString == null) {
            throw new IOException();
        }

        try {
            URL url = new URL(urlString);
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
        catch (IOException e) {
            Log.e(LOG_NAME, "IOException: Failed to fetch image");
            throw e;
        }
    }

    private static InputStream getInputStream(URL url) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setDoInput(true);
        conn.connect();
        InputStream in = conn.getInputStream();
        return in;
    }

    /**
     * Finds the input stream to the XML content for the given TVDBID
     */
    private static InputStream getInputStream(String url)
    {
        //String url = String.format(BANNER_API_URL, TVDB_API_KEY, tvdbid);

        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpClient client = new DefaultHttpClient(params);
        HttpGet get = new HttpGet(url);

        try {
            HttpResponse response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                return is;
            }
            else {
                Log.e(LOG_NAME, "Unable to contact TheTVDB");
                return null;
            }
        }
        catch (ClientProtocolException e) {
            Log.e(LOG_NAME, "Client Protocol Error");
            return null;
        }
        catch (IOException e) {
            Log.e(LOG_NAME, "IO Error");
            return null;
        }
    }

    /**
     * Gets the url to the fanart bitmap for the given tvdbid
     *
     * @param  tvdbid  tvdbid of the show to search for artwork
     *
     * @return
     */
    private static String getFanartUrl(String tvdbid)
    {
        String url = null;
        String bannerPath = "";
        String inputUrl = String.format(BANNER_API_URL, TVDB_API_KEY, tvdbid);

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            InputStream is = getInputStream(inputUrl);
            if (is == null) {
                throw new IOException();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            parser.setInput(reader);

            int eventType = parser.getEventType();
            boolean done = false;
            boolean isBannerPath = false;
            boolean isBannerType = false;

            while(eventType != XmlPullParser.END_DOCUMENT && !done) {
                String tagName = parser.getName();

                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("BannerPath")) {
                            isBannerPath = true;
                        }
                        else if (tagName.equalsIgnoreCase("BannerType")) {
                            isBannerType = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (isBannerPath) {
                            bannerPath = parser.getText();
                            isBannerPath = false;
                        }
                        else if (isBannerType) {
                            if ("fanart".equalsIgnoreCase(parser.getText())) {
                                done = true;
                            }
                            isBannerType = false;
                        }
                }

                eventType = parser.next();
            }

            url = String.format(BANNER_URL_PREFIX, bannerPath);
            return url;
        }
        catch (XmlPullParserException e) {
            Log.e(LOG_NAME, "XmlPullParserError: Parser failed");
            return null;
        }
        catch (IOException e) {
            Log.e(LOG_NAME, "IOException: No stream available");
            return null;
        }
    }

    public static String getSeriesOverview(Show show)
    {
        String url = String.format(SERIES_API_URL, show.getTitle().replaceAll(" ", "_").replaceAll("&", "-"));

        InputStream is = getInputStream(url);

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            if (is == null) {
                throw new IOException();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            parser.setInput(reader);

            int eventType = parser.getEventType();
            boolean isSeriesId = false;
            boolean isCorrectSeries = false;
            boolean isOverview = false;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("seriesid")) {
                            isSeriesId = true;
                        }
                        else if (tagName.equalsIgnoreCase("Overview")) {
                            isOverview = true;
                        }
                    break;

                    case XmlPullParser.TEXT:
                        if (isSeriesId && parser.getText().equalsIgnoreCase(show.getTvdbid())) {
                            isCorrectSeries = true;
                            isSeriesId = false;
                        }
                        else if (isCorrectSeries && isOverview) {
                                return parser.getText();
                        }
                }

                eventType = parser.next();
            }

            return null;
        }
        catch (XmlPullParserException e) {
            Log.e(LOG_NAME, "XmlPullParserError: Parser failed");
            return null;
        }
        catch (IOException e) {
            Log.e(LOG_NAME, "IOException: No stream available");
            return null;
        }

    }

}