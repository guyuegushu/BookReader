package guyuegushu.myownapp.Util;

import android.content.Context;
import android.widget.Toast;

import guyuegushu.myownapp.StaticGlobal.GlobalApplication;

/**
 * Created by guyuegushu on 2016/10/11.
 * 自定义Toast
 */
public class ToastUtil {
    private static Toast mToast;
    //信息为字符
    public static void showToast(Context context, String msg, int mDuration){
        if(mToast == null) {
            mToast = Toast.makeText(context, msg, mDuration);
        } else {
            mToast.setText(msg);
            mToast.setDuration(mDuration);
        }
        mToast.show();
    }
    //信息为资源ID（strings.xml）
    public static void showToast(Context context, int resId, int mDuration){
        showToast(GlobalApplication.getContext(), context.getString(resId), mDuration);
    }


}
