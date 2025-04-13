package com.pac.ovum;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class OvumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}
