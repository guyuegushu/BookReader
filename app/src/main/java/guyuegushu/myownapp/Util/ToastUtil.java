package guyuegushu.myownapp.Util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import guyuegushu.myownapp.StaticGlobal.GlobalApplication;

/**
 * Created by guyuegushu on 2016/10/11.
 * 自定义Toast
 */
public class ToastUtil {
    private static Toast mToast;

    //信息为字符
    public static void showToast(String msg, int mDuration) {

        if (isMainThread()) {
            createMyToast(msg, mDuration);
        } else {
            Looper.prepare();
            createMyToast(msg, mDuration);
            Looper.loop();
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private static void createMyToast(String msg, int mDuration) {
        if (mToast == null) {
            mToast = Toast.makeText(GlobalApplication.getContext(), msg, mDuration);
        } else {
            mToast.setText(msg);
            mToast.setDuration(mDuration);
        }
        mToast.show();
    }
    //信息为资源ID（strings.xml）
    public static void showToast(int resId, int mDuration){
        showToast(GlobalApplication.getContext().getString(resId), mDuration);
    }


}
