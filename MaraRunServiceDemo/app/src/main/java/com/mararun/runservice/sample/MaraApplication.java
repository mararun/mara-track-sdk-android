package com.mararun.runservice.sample;

import android.app.Application;

import com.mararun.runservice.engine.MaraTrackerManager;
import com.mararun.runservice.util.MaraLogger;

/**
 * Created by mararun
 */

public class MaraApplication extends Application {
    private static String sTrackEngineAuthResult = "NOT DONE YET...";

    public static String getTrackEngineAuthResult() {
        return sTrackEngineAuthResult;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MaraTrackerManager.getInstance().init(
                this,
                Constants.TRACK_ENGINE_KEY,
                (result, reason) -> {
                    MaraLogger.i("track engine auth result:" + result);
                    sTrackEngineAuthResult = String.valueOf(result) + "," + reason;
                }
        );
    }
}
