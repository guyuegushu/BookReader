package com.example.veb.bookreader;

import android.app.Application;
import android.content.Context;

/**
 * Created by VEB on 2016/10/25.
 */
public class GlobalApplication extends Application {

    private static Context context;
    private static CharacterParser parser;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        parser = CharacterParser.getInstance();
    }

    public static Context getContext() {
        return context;
    }
    public static CharacterParser getParser() {
        return parser;
    }

}
