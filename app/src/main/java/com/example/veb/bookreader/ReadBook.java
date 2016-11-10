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
 */
public class ReadBook extends Activity {


//    private final static String TAG = "ReadBook";
    private TextView display;
    private DBManager dbManager;
    private ScrollView scrollView;
    private int flags = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        dbManager = new DBManager(this);
//        display = (TextView) findViewById(R.id.display);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        isTopOrButton();

    }

    private String getBookPath() {
        String bookPath = null;

        Intent tmp = getIntent();
        bookPath = tmp.getStringExtra("bookPath");

        return bookPath;
    }

    private void isTopOrButton() {

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int viewOutside = view.getScrollY();
                int viewInside = view.getHeight();
                int viewHeight = scrollView.getChildAt(0).getMeasuredHeight();
                int skipNum = 1;
                String tmp = "";
//                view.getParent().requestDisallowInterceptTouchEvent(true);

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (viewOutside == 0) {
                            skipNum = dbManager.getSkipNum(getBookPath());
                            if (skipNum > 1) {//不是最开端
                                flags = 1;//向前阅读
                                skipNum -= 1;
                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);

                            } else {
                                flags = 2;//初始化
                                skipNum = 1;
                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);
                            }
                            dbManager.setSkipNum(getBookPath(), skipNum);
                            display = (TextView) findViewById(R.id.display);
                            scrollView.scrollTo(0, viewHeight);
                            display.setText(tmp);


                        } else if (viewHeight <= (viewOutside + viewInside)) {
                            skipNum = dbManager.getSkipNum(getBookPath());

                            if (skipNum > 1) {
                                flags = 2;//向后阅读
                                skipNum += 1;

                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);
                                flags = 0;
                            } else {
                                flags = 1;
                                skipNum = 2;

                                tmp = dbManager.getBookFromDb(getBookPath(), skipNum, flags);
                            }
                            dbManager.setSkipNum(getBookPath(), skipNum);
                            display = (TextView) findViewById(R.id.display);
                            scrollView.scrollTo(0, -viewHeight);//很好
                            display.setText(tmp);
                        }

                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        Log.d(TAG, "###############成功返回标记");
//        setResult(RESULT_OK);
//        super.onBackPressed();
//    }

}
