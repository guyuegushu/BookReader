package com.example.veb.bookreader;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * Created by Administrator on 2017/2/15.
 */

public class MyActivityManager {

    /* 私有构造方法，防止被实例化 */
    private MyActivityManager() {

    }

    /* 此处使用一个内部类来维护单例 */
    private static class Factory {
        private static MyActivityManager managerInstance = new MyActivityManager();
    }

    /* 获取实例 */
    public static MyActivityManager getInstance() {
        return Factory.managerInstance;
    }

    /* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */
    public Object readResolve() {
        return getInstance();
    }

    private static Stack<Activity> activityStack;

    /*添加Activity到堆栈*/
    public void addActivity(Activity activity) {

        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /*获取当前Activity（堆栈中最后一个压入的）*/
    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    /*结束当前Activity（堆栈中最后一个压入的）*/
    public void finishCurrentActivity() {
        finishTargetActivity(activityStack.lastElement());
    }

    /*结束指定的Activity*/
    public void finishTargetActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /*通过类名来结束Activity*/
    public void finishActivityByClassName(Class<?> cls) {
        for (Activity a : activityStack) {
            if (a.getClass().equals(cls)) {
                finishTargetActivity(a);
            }
        }
    }

    /*结束所有Activity*/
    public void finishAllActivity() {
        for (Activity a : activityStack) {
            finishTargetActivity(a);
        }
        activityStack.clear();
    }

    public void exit(Context context) {

        finishAllActivity();
        ActivityManager m = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        m.restartPackage(context.getPackageName());
        System.exit(0);
    }

}
