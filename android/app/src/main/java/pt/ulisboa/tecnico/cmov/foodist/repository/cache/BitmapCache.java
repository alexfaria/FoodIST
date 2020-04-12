package pt.ulisboa.tecnico.cmov.foodist.repository.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.isExternalStorageRemovable;

public class BitmapCache {

    private LruCache<String, Bitmap> memoryCache;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 100; // 100 MB
    private static final String DISK_CACHE_SUBDIR = "dishes";

    public BitmapCache(Context context) {
        File cacheDir = getDiskCacheDir(context);
        new InitDiskCacheTask().execute(cacheDir);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, APP_VERSION, VALUE_COUNT, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDiskCacheStarting = false; // Finished initialization
        }
    }

    public interface DecodingCallback {
        void done(Bitmap bitmap);
    }

    public void get(String key, DecodingCallback callback) {
        // First try to retrieve from memory cache
        Bitmap bitmap = memoryCache.get(key);
        if (bitmap == null)
            new DecodingTask(callback).execute(key);
        else callback.done(bitmap);
    }

    private class DecodingTask extends AsyncTask<String, Void, Bitmap> {

        private DecodingCallback callback;

        private DecodingTask(DecodingCallback callback) {
            this.callback = callback;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            final String imageKey = String.valueOf(params[0]);
            DiskLruCache.Snapshot foundSnapshot = null;
            synchronized (mDiskCacheLock) {
                // Wait while disk cache is started from background thread
                while (mDiskCacheStarting) {
                    try {
                        mDiskCacheLock.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                if (mDiskLruCache != null) {
                    try {
                        foundSnapshot = mDiskLruCache.get(imageKey);
                    } catch (Exception e) {}
                }
            }
            if (foundSnapshot != null) try(DiskLruCache.Snapshot snapshot = foundSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                memoryCache.put(imageKey, bitmap);
                return bitmap;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            callback.done(bitmap);
        }
    }

    public void put(String key, Bitmap bitmap) {
        // Add if not exists in memory cache
        // If exists, it certainly also exists in disk, so there is no need to add
        if (memoryCache.get(key) == null) {
            // Add to memory cache
            memoryCache.put(key, bitmap);
            // Also add to disk cache
            new EncodingTask(key).execute(bitmap);
        }
    }

    private class EncodingTask extends AsyncTask<Bitmap, Void, Void> {

        private String key;

        private EncodingTask(String key) {
            this.key = key;
        }

        // Encode image in background.
        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            synchronized (mDiskCacheLock) {
                DiskLruCache.Editor editor = null;
                try {
                    if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                        editor = mDiskLruCache.edit(key);
                        bitmaps[0].compress(Bitmap.CompressFormat.PNG, 95, editor.newOutputStream(0));
                        editor.commit();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (editor != null) {
                        try {
                            editor.abort();
                        } catch (IOException ignored) { }
                    }
                }
            }
            return null;
        }
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    private static File getDiskCacheDir(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();
        return new File(cachePath + File.separator + DISK_CACHE_SUBDIR);
    }

    public void close() {
        synchronized (mDiskCacheLock) {
            try {
                if (!mDiskLruCache.isClosed()) {
                    mDiskLruCache.close();
                }
                mDiskLruCache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
