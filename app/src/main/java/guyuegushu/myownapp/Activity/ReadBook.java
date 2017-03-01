package guyuegushu.myownapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import guyuegushu.myownapp.Dao.DBManager;
import guyuegushu.myownapp.R;
import guyuegushu.myownapp.StaticGlobal.MyActivityManager;
import guyuegushu.myownapp.Util.LogUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by guyuegushu on 2016/10/25.
 * 上下滑动翻页
 */
public class ReadBook extends Activity {

    private TextView display;
    private DBManager dbManager;
    private ScrollView scrollView;
    private int skipNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().addActivity(ReadBook.this);
        setContentView(R.layout.activity_display);
        dbManager = new DBManager(this);
        display = (TextView) findViewById(R.id.display);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        skipNum = dbManager.getSkipNum(getBookPath());
        init();
        reading();
    }

    private void init() {
        if (!dbManager.isFirstOpen(getBookPath())) {

            display.setText(getBookContent(skipNum));
        } else {
            LogUtil.d("不是第一次打开");
        }
    }

    private String getBookPath() {
        String bookPath = null;

        Intent tmp = getIntent();
        bookPath = tmp.getStringExtra("bookPath");

        return bookPath;
    }

    private String getBookContent(int localSkipNum) {

        String cache = "";
        try {
            cache = new String(dbManager.openBook(getBookPath(), localSkipNum), "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cache;
    }

    private void isAheadTo() {
        skipNum = skipNum - 1;
        LogUtil.d("" + skipNum);
    }

    private void isBackwardTo() {
        skipNum = skipNum + 1;
        LogUtil.d("" + skipNum);
    }

    private void reading() {
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            int totalMove = 0;
            int startY = 0;
            int endY = 0;
            String cache = "";

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
                            if (dbManager.isFirstPage(skipNum)) {
                                skipNum = 1;
                                cache = getBookContent(skipNum);
                                display.setText(cache);
                            } else {
                                isAheadTo();
                                cache = getBookContent(skipNum);
                                scrollView.scrollTo(0, viewHeight);
                                display.setText(cache);
                            }


                            //这里不能是 >=
                            // 原因：getScrollY()值不是绝对靠谱的，它会超过边界值，
                            // 但是它自己会恢复正确，导致上面的计算条件不成立
                            // 仔细想想也感觉想得通，系统的ScrollView在处理滚动的时候动态计算
                            // 那个scrollY的时候也会出现超过边界再修正的情况
                        } else if (viewHeight == (viewOutside + viewInside)) {
                            if (dbManager.isFirstPage(skipNum)) {
                                isBackwardTo();
                                cache = getBookContent(skipNum);
                                scrollView.scrollTo(0, -viewHeight);
                                display.setText(cache);
                            } else {
                                isBackwardTo();
                                cache = getBookContent(skipNum);
                                scrollView.scrollTo(0, -viewHeight);
                                display.setText(cache);
                            }
                        }
                        return false;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {

        dbManager.saveSkipNum(getBookPath(), skipNum);
        LogUtil.d("" + skipNum);
        super.onDestroy();
    }
}
