package guyuegushu.myownapp.StaticGlobal;

import android.app.Application;
import android.content.Context;

/**
 * Created by guyuegushu on 2016/10/25.
 *
 */
public class GlobalApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
