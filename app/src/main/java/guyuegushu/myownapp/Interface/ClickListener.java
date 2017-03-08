package guyuegushu.myownapp.Interface;

import android.view.View;

/**
 * Created by guyuegushu on 2016/10/11.
 * 点击事件和长按事件的回调接口
 */
public interface ClickListener {
    void onClicks(View item, View widget, int position, int which);
    void onLongClicks(View item, View parents, int position, int ids);
}
