package com.richfield.smartpantry.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class used to run tasks on the appropriate Android thread.
 *
 * Database operations are placed on a background thread so that
 * database work does not block the Android user interface.
 *
 * Tasks that need to update the interface can then be posted back
 * to the Android main thread.
 */
public final class AppExecutors {

    // Single background thread used for database operations.
    private static final ExecutorService DATABASE = Executors.newSingleThreadExecutor();

    // Handler connected to Android's main UI thread.
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    /**
     * Private constructor prevents this utility class from being
     * instantiated.
     */
    private AppExecutors() {
    }

    /**
     * Runs the supplied task on the database background thread.
     *
     * @param task operation that should run away from the UI thread
     */
    public static void database(Runnable task) {
        DATABASE.execute(task);
    }

    /**
     * Posts the supplied task to Android's main UI thread.
     *
     * @param task operation that needs to run on the UI thread
     */
    public static void main(Runnable task) {
        MAIN.post(task);
    }
}