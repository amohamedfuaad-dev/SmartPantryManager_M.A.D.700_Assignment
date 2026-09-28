package com.richfield.smartpantry.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Keeps database work off the Android main thread.
 */
public final class AppExecutors {

    private static final ExecutorService DATABASE = Executors.newSingleThreadExecutor();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private AppExecutors() {
    }

    public static void database(Runnable task) {
        DATABASE.execute(task);
    }

    public static void main(Runnable task) {
        MAIN.post(task);
    }
}
