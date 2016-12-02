package com.example.veb.bookreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by VEB on 2016/10/25.
 * 上下滑动翻页
 */
public class ReadBook extends Activity {

    private TextView display;
    private DBManager dbManager;
    private ScrollView scrollView;
    private int flags = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        dbManager = new DBManager(this);
        display = (TextView) findViewById(R.id.display);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
//        isTopOrButton();
    }

    private String getBookPath() {
        String bookPath = null;

        Intent tmp = getIntent();
        bookPath = tmp.getStringExtra("bookPath");

        return bookPath;
    }

    

    private void isTopOrButton() {
        scrollView.setOnTouchListener(new View.OnTouchListener() {

            int totalMove = 0;
            int startY = 0;
            int endY = 0;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int viewOutside = view.getScrollY();
                int viewInside = view.getHeight();
                int viewHeight = scrollView.getChildAt(0).getMeasuredHeight();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        startY = (int) motionEvent.getY();
                        totalMove = 0;
                        return false;

                    case MotionEvent.ACTION_MOVE:

                        endY = (int) motionEvent.getY();
                        totalMove += endY - startY;
                        return false;

                    case MotionEvent.ACTION_UP:

                        if (viewOutside == 0) {//这里不能是getScrollY() <= 0

                            //这里不能是 >=
                            // 原因：getScrollY()值不是绝对靠谱的，它会超过边界值，
                            // 但是它自己会恢复正确，导致上面的计算条件不成立
                            // 仔细想想也感觉想得通，系统的ScrollView在处理滚动的时候动态计算
                            // 那个scrollY的时候也会出现超过边界再修正的情况
                        } else if (viewHeight == (viewOutside + viewInside)) {

                        }
                        return false;
                    default:
                        break;
                }
                return false;
            }
        });
    }

//    private void isTopOrButton() {
//        scrollView.setOnTouchListener(new View.OnTouchListener() {
//            int skipNum = 1;
//            String tmp = "";
//
//            int totalMove = 0;
//            int startY = 0;
//            int endY = 0;
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                int viewOutside = view.getScrollY();
//                int viewInside = view.getHeight();
//                int viewHeight = scrollView.getChildAt(0).getMeasuredHeight();
//
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startY = (int) motionEvent.getY();
//                        totalMove = 0;
//                        return false;
//                    case MotionEvent.ACTION_MOVE:
//                        endY = (int) motionEvent.getY();
//                        totalMove += endY - startY;
//                        return false;
//                    case MotionEvent.ACTION_UP:
//
//                        dbManager.setSkipNum(getBookPath(), skipNum);
//
//                        if (viewOutside == 0) {//这里不能是getScrollY() <= 0
//                            skipNum = dbManager.getSkipNum(getBookPath());
//                            if (skipNum > 1) {//不是最开端
//                                flags = 1;//向前阅读
//                                skipNum -= 1;
//                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);
//                                scrollView.scrollTo(0, viewHeight);//当不在最开端的时候进行滚动
//                            } else {
//                                flags = 2;//初始化
//                                skipNum = 1;
//                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);
//                            }
//                            display.setText(tmp);
//                            //这里不能是 >=
//                            // 原因：getScrollY()值不是绝对靠谱的，它会超过边界值，
//                            // 但是它自己会恢复正确，导致上面的计算条件不成立
//                            // 仔细想想也感觉想得通，系统的ScrollView在处理滚动的时候动态计算
//                            // 那个scrollY的时候也会出现超过边界再修正的情况
//                        } else if (viewHeight == (viewOutside + viewInside)) {
//                            skipNum = dbManager.getSkipNum(getBookPath());
//                            if (skipNum > 1) {
//                                flags = 2;//向后阅读
//                                skipNum += 1;
//                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);
//                                flags = 0;
//                            } else {
//                                flags = 1;
//                                skipNum = 2;
//                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);
//                            }
//                            scrollView.scrollTo(0, -viewHeight);
//                            display.setText(tmp);
//                        }
//                        return false;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
//    }

}
