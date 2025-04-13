package com.pac.ovum.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Object LOCK = new Object();
    private static AppExecutors instance;
    private final ExecutorService diskIO;
    private final ExecutorService networkIO;
    private final Executor mainThread;

    private AppExecutors(ExecutorService diskIO, ExecutorService networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new AppExecutors(
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return instance;
    }

    public ExecutorService diskIO() {
        return diskIO;
    }
    
    public ExecutorService networkIO() {
        return networkIO;
    }
    
    public Executor mainThread() {
        return mainThread;
    }
    
    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        
        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
