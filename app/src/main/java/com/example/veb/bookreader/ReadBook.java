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
                int totalMove = 0;
                int
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                         (int)motionEvent.getX();



                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (viewOutside == 0) {//这里不能是getScrollY() <= 0
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

                            //这里不能是 >=
                            // 原因：getScrollY()值不是绝对靠谱的，它会超过边界值，
                            // 但是它自己会恢复正确，导致上面的计算条件不成立
                            // 仔细想想也感觉想得通，系统的ScrollView在处理滚动的时候动态计算
                            // 那个scrollY的时候也会出现超过边界再修正的情况
                        } else if (viewHeight == (viewOutside + viewInside)) {
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

//    public boolean onTouch(View v, MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//                lastX = (int)event.getX();
//                totalMove = 0;
//                firstDown = false;
//                return false;
//            }
//            case MotionEvent.ACTION_MOVE:{
//                if (firstDown) {
//                    curX = (int) event.getX();
//                    totalMove = 0;
//                    firstDown = false;
//                }
//                curX = (int) event.getX();
//                int delatX = curX - lastX;
//                totalMove += delatX;
//                lastX = curX;
//                return false;
//            }
//            case MotionEvent.ACTION_UP:{
//                boolean result = false;
//                if(totalMove > 20 ){
//                    totalMove = 0;
////things you shouold do here
//                    result = true;
//                }
//                if(totalMove < 0 && Math.abs(totalMove) > 20){
//                    totalMove = 0;
////things you shouold do here
//                    result= true;
//                }
//                return result;
//            }
//            return false;
//        }
//    };

}
