package com.example.veb.bookreader;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by VEB on 2016/11/18.
 */

public class LogUtil {


    private static String getTag() {
//        StackTraceElement[] trace = new Throwable()
//                .fillInStackTrace().getStackTrace();

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();


        String tag = "%s.%s(Line:%d)";//类名.方法（行数）
        for (int i = 2; i < trace.length; i++) {
            Class tmp = trace[i].getClass();
            if (!tmp.equals(LogUtil.class)) {

                String callerName = trace[i].getClassName();
                String callerMethod = trace[i].getMethodName();
                int callerLine = trace[i].getLineNumber();

                callerName = callerName.substring(callerName.
                        lastIndexOf(".") + 1);

                tag = String.format(tag, callerName,
                        callerMethod, callerLine);

                return tag;

            }

        }
        if (TextUtils.isEmpty(tag.trim())) {
            tag = "NULL";
            return tag;

        } else {
            return tag;
        }
    }

    private static String formatMsg(String msg) {
        String msgFormat = "#################>>%s";
        return String.format(msgFormat, msg);
    }

    public static void v(String msg) {
        String finalMsg = formatMsg(msg);
        Log.v(getTag(), finalMsg);
    }

    public static void d(String msg) {
        String finalMsg = formatMsg(msg);
        Log.d(getTag(), finalMsg);
    }

    public static void i(String msg) {
        String finalMsg = formatMsg(msg);
        Log.i(getTag(), finalMsg);
    }

    public static void w(String msg) {
        String finalMsg = formatMsg(msg);
        Log.w(getTag(), finalMsg);
    }

    public static void e(String msg) {
        String finalMsg = formatMsg(msg);
        Log.e(getTag(), finalMsg);
    }

}
