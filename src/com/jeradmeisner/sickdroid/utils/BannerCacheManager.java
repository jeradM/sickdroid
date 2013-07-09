package com.jeradmeisner.sickdroid.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

public class BannerCacheManager {

    /**
     * Enum type for different bitmap types that can be saved to cache
     * ** The toString() value will be the directory name **
     */
    public enum BitmapType {
        BANNER("banner"), POSTER("poster"), FANART("fanart");

        private String bitmapTypeString;

        private BitmapType(String type) {
            bitmapTypeString = type;
        }

        public String toString()
        {
            return bitmapTypeString;
        }
    }

    /**
     * The directory to use for storing files in disk cache
     */
    private static final String CACHE_DIRECTORY = "bitmaps";

    /**
     * The memory cache used to store banners for quicker access
     */
    private LruCache<String, Bitmap> memoryCache;

    /**
     * Reference to the cache directory on external storage
     */
    private File cacheDir;

    /**
     * Singleton instance of class
     */
    private static BannerCacheManager instance;

    /**
     * Obtain reference to the singleton instance
     *
     * @param  context  the context requesting the reference
     *
     * @return singleton reference to this class
     */
    public static BannerCacheManager getInstance(Context context)
    {
        if (instance == null) {
            instance = new BannerCacheManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructor to create cache directories if needed
     *
     * @param  context  the application context requesting the instance
     */
    private BannerCacheManager(Context context)
    {
        context = context.getApplicationContext();
        this.cacheDir = new File(context.getExternalCacheDir(), CACHE_DIRECTORY);
        if (!this.cacheDir.exists()) {
            this.cacheDir.mkdirs();
        }

        for (BitmapType type : BitmapType.values()) {
            File dir = new File(cacheDir, type.toString());
            if (!dir.exists())
                dir.mkdirs();
        }

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        //int memoryClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        //int cacheSize = memoryClass * 1024 * 1024 / 8;
        int cacheSize = maxMemory / 4;

        this.memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String id, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    /**
     * Does the given id have an image in cache already
     *
     * @param  id  the tvdbid of the show
     *
     * @return true if image is in cache
     */
    public boolean contains(String id, BitmapType type)
    {
        return memoryContains(id, type) || diskContains(id, type);
    }

    /**
     * Does the memory cache contain the bitmap
     *
     * @param  id  the tvdbid
     *
     * @return true if the bitmap is in memory cache
     */
    private boolean memoryContains(String id, BitmapType type)
    {
        id = clean(id);
        return memoryCache.get(id + type.toString()) != null;
    }

    /**
     * Does the disk cache contain the target bitmap
     *
     * @param  id  the tvdbid
     *
     * @return true if the bitmap exists in disk cache
     */
    private boolean diskContains(String id, BitmapType type)
    {
        id = clean(id);

        try {
            File tmpDir = new File(cacheDir, type.toString());
            if (tmpDir.canRead()) {
                File target = new File(tmpDir, id);
                return target.exists();
            }
        }
        catch (Exception e) {
            Log.e("Disk Read Error", "Unable to read image from disk");
        }

        return false;
    }

    /**
     * Adds a bitmap to the memory and disk caches
     *
     * @param  id     the tvdbid - this will be the filename
     * @param  bitmap  the bitmap to add to the cache
     */
    public void addBitmap(String id, Bitmap bitmap, BitmapType type)
    {
        id = clean(id);

        if (type == BitmapType.BANNER) {
            synchronized (memoryCache) {
                memoryCache.put(id + type.toString(), bitmap);
            }
        }

        try {
            File tmpDir = new File(cacheDir, type.toString());
            FileOutputStream out = new FileOutputStream(new File(tmpDir, id));
            bitmap.compress(CompressFormat.JPEG, 90, out);
            out.close();
            Log.i("BannerCacheManager", "Added \"" + id + "\" to disk cache");
        }
        catch (IOException e) {
            Log.e("BannerCacheManager",	"Error adding bitmap to cache");
        }
    }

    /**
     * Removes a bitmap from the disk cache
     *
     * @param  id  the tvdbid of the key to remove
     */
    public void removeFromDiskCache(String id, BitmapType type) {
        id = clean(id);
        try {
            File tmpDir = new File(cacheDir, type.toString());
            File file = new File(tmpDir, id);
            if (file.delete()) {
                Log.i("BannerCacheManager", "File deleted");
            }
            else {
                throw new IOException();
            }
        }
        catch (IOException e) {
            Log.e("BannerCacheManager", "Unable to delete file");
        }
    }

    /**
     * Gets the bitmap associated with the given key
     *
     * @param  id  the tvdbid
     *
     * @return the banner for the given key or null if not in cache
     */
    public Bitmap get(String id, BitmapType type)
    {
        id = clean(id);
        /*if (memoryContains(id, type)) {
            return getFromMemory(id, type);
        }
        else if (diskContains(id, type)) {
            return getFromDisk(id, type);
        }
        else {
            return null;
        }*/

        Bitmap b = getFromMemory(id, type);

        if (b != null)
            return b;
        else
            return getFromDisk(id, type);
    }

    /**
     * Gets the requested bitmap from memory cache
     */
    private Bitmap getFromMemory(String id, BitmapType type)
    {
        return memoryCache.get(id + type.toString());
    }

    /**
     * Gets the requested bitmap from disk cache
     */
    private Bitmap getFromDisk(String id, BitmapType type)
    {
        try {
            File tmpDir = new File(cacheDir, type.toString());
            File file = new File(tmpDir, id);
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();

                // Add bitmap to memory cache
                if (id != null && bitmap != null && type == BitmapType.BANNER) {
                    synchronized (memoryCache) {
                        memoryCache.put(id + type.toString(), bitmap);
                    }
                }
                return bitmap;
            }
        }
        catch (IOException e) {
            Log.e("BannerCacheManager", "Unable to retrieve image from disk cache.");
        }

        return null;
    }

    /**
     * Clears all memory and disk cache
     *
     * @return true is clear was successful
     */
    public boolean clearCache()
    {
        try {
            clearMemoryCache();
            clearDiskCache();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Clears the memory cache
     */
    private void clearMemoryCache()
    {
        synchronized (memoryCache) {
            memoryCache.evictAll();
        }
    }

    /**
     * Clears the disk cache
     */
    private void clearDiskCache() throws IOException
    {
        for (File dir : cacheDir.listFiles()) {
            for (File file : dir.listFiles()) {
                if (file.isFile())
                    file.delete();
            }
        }
    }

    /**
     * Clean the given string to remove illegal characters
     *
     * @param  dirty  the string to clean
     *
     * @return the sanitized string
     */
    private String clean(String dirty)
    {
        return dirty.replaceAll("[.:/,%?&=]", "_").replaceAll("_+", "_");
    }

}